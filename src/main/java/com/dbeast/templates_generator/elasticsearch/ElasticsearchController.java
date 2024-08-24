package com.dbeast.templates_generator.elasticsearch;

import com.dbeast.templates_generator.exceptions.ClusterConnectionException;
import com.dbeast.templates_generator.templates_generator.pojo.EsSettingsPOJO;
import com.dbeast.templates_generator.templates_generator.pojo.ui_pojo.project_settings.InputSettingsPOJO;
import com.dbeast.templates_generator.templates_generator.pojo.ui_pojo.project_settings.ScrollResultsPOJO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.search.*;
import org.elasticsearch.client.*;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.client.indices.*;
import org.elasticsearch.cluster.metadata.ComposableIndexTemplate;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.metrics.Max;
import org.elasticsearch.search.aggregations.metrics.MaxAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Min;
import org.elasticsearch.search.aggregations.metrics.MinAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.util.*;

public class ElasticsearchController {
    private static final Logger logger = LogManager.getLogger();
    private final ObjectMapper mapper = new ObjectMapper();

    private final ElasticsearchDbProvider elasticsearchClient = new ElasticsearchDbProvider();

    public boolean isTemplateExistsOld(final RestHighLevelClient client,
                                    final String templateName) {
        GetIndexTemplatesResponse response;
        try {
            GetIndexTemplatesRequest request = new GetIndexTemplatesRequest(templateName);
            response = client.indices().getIndexTemplate(request, RequestOptions.DEFAULT);
            return response.getIndexTemplates().size() > 0;
        } catch (IOException | ElasticsearchStatusException e) {
            return false;
        }
    }

    //TODO add ES version verification
    public boolean isTemplateExists(final RestHighLevelClient client,
                                    final String templateName) {
        boolean result;
        try {
            IndexTemplatesExistRequest legacyTemplateRequest = new IndexTemplatesExistRequest(templateName);
            result =  client.indices().existsTemplate(legacyTemplateRequest, RequestOptions.DEFAULT);
            if (!result) {
                GetComposableIndexTemplateRequest composableTemplateRequest = new GetComposableIndexTemplateRequest(templateName);
                GetComposableIndexTemplatesResponse getTemplatesResponse = client.indices().getIndexTemplate(composableTemplateRequest, RequestOptions.DEFAULT);
                Map<String, ComposableIndexTemplate> templates = getTemplatesResponse.getIndexTemplates();
                return templates.size() > 0;
            }
            return true;
        } catch (IOException | ElasticsearchStatusException e) {
            return false;
        }
    }

    public long getDocsCount(final RestHighLevelClient client,
                             final String indexName) {
        CountRequest countRequest = new CountRequest(indexName);
        CountResponse countResponse = null;
        try {
            countResponse = client
                    .count(countRequest, RequestOptions.DEFAULT);
        } catch (IOException | ElasticsearchStatusException e) {
            return 0;
        }
        if (countResponse != null) {
            return countResponse.getCount();
        } else {
            return 0;
        }
    }

    public ScrollResultsPOJO searchWithScrollWithScrollId(String scrollId,
                                                          RestHighLevelClient client) throws IOException {

        ScrollResultsPOJO scrollResults = new ScrollResultsPOJO();
        final Scroll scroll = new Scroll(TimeValue.timeValueMinutes(5L));
        SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
        scrollRequest.scroll(scroll);
        SearchResponse searchResponse = client.scroll(scrollRequest, RequestOptions.DEFAULT);
        scrollResults.setScrollId(searchResponse.getScrollId());
        scrollResults.setResults(Arrays.asList(searchResponse.getHits().getHits()));
//        logger.info("scroll size: " + scrollResults.getResults().size());
        return scrollResults;
    }

    public boolean closeScrollRequest(final String scrollId, RestHighLevelClient client) throws IOException {
        ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
        clearScrollRequest.addScrollId(scrollId);
        ClearScrollResponse clearScrollResponse = client.clearScroll(clearScrollRequest, RequestOptions.DEFAULT);

        return clearScrollResponse.isSucceeded();
    }

    public ScrollResultsPOJO searchWithScrollFirstRequest(final InputSettingsPOJO actions,
                                                          RestHighLevelClient client) throws IOException {
        ScrollResultsPOJO scrollResults = new ScrollResultsPOJO();
        final Scroll scroll = new Scroll(TimeValue.timeValueMinutes(5L));
        SearchRequest searchRequest = new SearchRequest(actions.getIndexForAnalyze());
        searchRequest.scroll(scroll);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.size(actions.getScrollSize());
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        scrollResults.setScrollId(searchResponse.getScrollId());
        scrollResults.setResults(Arrays.asList(searchResponse.getHits().getHits()));

        return scrollResults;
    }

    public long getIndexDocsCount(final EsSettingsPOJO connectionSettings,
                                  final String projectId,
                                  final String index) throws ClusterConnectionException {
        RestHighLevelClient client = elasticsearchClient.getHighLevelClient(connectionSettings, projectId);
        CountRequest countRequest = new CountRequest(index);
        try {
            return client
                    .count(countRequest, RequestOptions.DEFAULT).getCount();
        } catch (IOException | ElasticsearchStatusException e) {
            logger.error(e);
            return 0;
        }
    }

    public String getTemplateParameters(final EsSettingsPOJO connectionSettings,
                                        final String template,
                                        final String projectId) throws ClusterConnectionException {
        RestClient client = elasticsearchClient.getLowLevelClient(connectionSettings, projectId);
        Response response;
        try {
            response = client.performRequest(new Request("GET", "_template/" + template));
            if (response.getStatusLine().getStatusCode() == 200) {
                // parse the JSON response
                return EntityUtils.toString(response.getEntity());
            } else {
                logger.error("There is the error in the get index parameters of index: " + template);
            }
        } catch (IOException e) {
            logger.error(e);
            return String.valueOf(e);
        }
        return null;
    }

    public String getIndexParameters(final EsSettingsPOJO connectionSettings,
                                     final String index,
                                     final String projectId) throws ClusterConnectionException {
        RestClient client = elasticsearchClient.getLowLevelClient(connectionSettings, projectId);
        Response response;
        try {
            response = client.performRequest(new Request("GET", index));
            if (response.getStatusLine().getStatusCode() == 200) {
                // parse the JSON response
                return EntityUtils.toString(response.getEntity());
            } else {
                logger.error("There is the error in the get index parameters of index: " + index);
            }
        } catch (IOException e) {
            logger.error(e);
            return String.valueOf(e);
        }
        return null;
    }

    public List<HashMap<String, String>> getIndexList(final EsSettingsPOJO connectionSettings,
                                                      final String projectId) {
        return getTemplateOrIndexList(connectionSettings, "/_cat/indices?h=index&format=json", projectId);
    }


    public List<HashMap<String, String>> getTemplateList(final EsSettingsPOJO connectionSettings,
                                                         final String projectId) {
        return getTemplateOrIndexList(connectionSettings, "/_cat/templates?h=name&format=json", projectId);
    }
    public Set<String> getLegacyTemplateList(final EsSettingsPOJO connectionSettings,
                                             final String projectId) {
//        return getTemplateOrIndexList(connectionSettings, "/_cat/templates?h=name&format=json", projectId);
        Response response;
        try {
            RestClient client = elasticsearchClient.getLowLevelClient(connectionSettings, projectId);
            response = client.performRequest(new Request("GET", "_template?filter_path=*.order"));
            if (response.getStatusLine().getStatusCode() == 200) {
                // parse the JSON response
                String rawBody = EntityUtils.toString(response.getEntity());
                TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {
                };
                HashMap<String, Object> templatesList = mapper.readValue(rawBody, typeRef);
                return templatesList.keySet();
            }
        } catch (IOException | ClusterConnectionException e) {
            logger.error(e.getMessage());
        }
        return new HashSet<>();
    }

    //TODO add catch exception
    private List<HashMap<String, String>> getTemplateOrIndexList(final EsSettingsPOJO connectionSettings,
                                                                 final String endPoint,
                                                                 final String projectId) {
        Response response;
        try {
            RestClient client = elasticsearchClient.getLowLevelClient(connectionSettings, projectId);
            response = client.performRequest(new Request("GET", endPoint));
            if (response.getStatusLine().getStatusCode() == 200) {
                // parse the JSON response
                String rawBody = EntityUtils.toString(response.getEntity());
                TypeReference<List<HashMap<String, String>>> typeRef = new TypeReference<List<HashMap<String, String>>>() {
                };
                return mapper.readValue(rawBody, typeRef);
            }
        } catch (IOException | ClusterConnectionException e) {
            logger.error(e.getMessage());
        }
        return new LinkedList<>();
    }

    public IndexTemplateMetadata getTemplateByName(final EsSettingsPOJO connectionSettings,
                                                   final String templateName,
                                                   final String projectId) {
        RestHighLevelClient client = null;
        try {
            client = elasticsearchClient.getHighLevelClient(connectionSettings, projectId);
        } catch (ClusterConnectionException e) {
            e.printStackTrace();
        }
        return getTemplateByName(client, templateName);
    }

    public IndexTemplateMetadata getTemplateByName(final RestHighLevelClient client,
                                                   final String templateName) {
        GetIndexTemplatesResponse response;
        try {
            GetIndexTemplatesRequest request = new GetIndexTemplatesRequest(templateName);
            response = client.indices().getIndexTemplate(request, RequestOptions.DEFAULT);
            IndexTemplateMetadata template;
            if (response.getIndexTemplates().size() > 0) {
                template = response.getIndexTemplates().get(0);
                return template;
            } else {
                System.out.println("Template doesn't exists");
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    public String getClusterStatus(final EsSettingsPOJO connectionSettings,
                                   final String projectId) throws
            ClusterConnectionException {
        RestHighLevelClient client = elasticsearchClient.getHighLevelClient(connectionSettings, projectId);
        try {
            ClusterHealthRequest request = new ClusterHealthRequest();
            request.timeout("30s");
            ClusterHealthResponse response = client.cluster().health(request, RequestOptions.DEFAULT);
            return response.getStatus().toString();
        } catch (Exception e) {
            throw new ClusterConnectionException(connectionSettings.getEs_host(), e);
        } finally {
            try {
                if (client != null) {
                    client.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public String getClusterStatus(final RestHighLevelClient client) throws
            ClusterConnectionException {
        try {
            ClusterHealthRequest request = new ClusterHealthRequest();
            request.timeout("30s");
            ClusterHealthResponse response = client.cluster().health(request, RequestOptions.DEFAULT);
            return response.getStatus().toString();
        } catch (Exception e) {
            throw new ClusterConnectionException(client.getLowLevelClient().getNodes().toString(), e);
        }
    }

    public DataPeriodFromEs getStartAndEndDateOfIndex(final RestHighLevelClient client,
                                                      final String index,
                                                      final String dateField) {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        MaxAggregationBuilder maxAggregation = AggregationBuilders.max("max").field(dateField);
        MinAggregationBuilder minAggregation = AggregationBuilders.min("min").field(dateField);
        searchSourceBuilder.aggregation(maxAggregation);
        searchSourceBuilder.aggregation(minAggregation);
        searchSourceBuilder.size(0);
        searchRequest.source(searchSourceBuilder);
        try {
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            Aggregations aggregations = searchResponse.getAggregations();
            Min min = aggregations.get("min");
            Max max = aggregations.get("max");

            return new DataPeriodFromEs(
                    (long)min.getValue(),
//                    new Double(min.getValue()).longValue(),
                    (long)max.getValue()
            );
        } catch (IOException | ElasticsearchException e) {
            logger.error(e);
            //TODO add index is empty exception
            return new DataPeriodFromEs(-1, -1);
        }
    }


}
