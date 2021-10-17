//package com.electusplus.index_analyzer.mapping_generator;
//
//import com.electusplus.index_analyzer.IndexAnalyzer;
//import com.electusplus.index_analyzer.IndexAnalyzerCLI;
//import com.electusplus.index_analyzer.constants.EAppSettings;
//import com.electusplus.index_analyzer.elasticsearch.ElasticsearchController;
//import com.electusplus.index_analyzer.elasticsearch.ElasticsearchDbProvider;
//import com.electusplus.index_analyzer.exceptions.ClusterConnectionException;
//import com.electusplus.index_analyzer.mapping_generator.cli_pojo.output.*;
//import com.electusplus.index_analyzer.mapping_generator.cli_pojo.project.*;
//import com.electusplus.index_analyzer.mapping_generator.data_analizers.*;
//import com.electusplus.index_analyzer.mapping_generator.pojo.DateFormatsPOJO;
//import com.electusplus.index_analyzer.utils.GeneralUtils;
//import com.fasterxml.jackson.core.JsonGenerator;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.node.*;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.elasticsearch.client.RestHighLevelClient;
//
//import java.io.BufferedReader;
//import java.io.FileNotFoundException;
//import java.io.FileReader;
//import java.io.IOException;
//import java.util.*;
//
//public class MappingGeneratorCLI {
//    private static final Logger logger = LogManager.getLogger();
//
//    private final ObjectMapper mapper = new ObjectMapper();
//    private final IntegerAnalyzer integerAnalyzer = new IntegerAnalyzer();
//    private final LongAnalyzer longAnalyzer = new LongAnalyzer();
//    private final BooleanAnalyzer booleanAnalyzer = new BooleanAnalyzer();
//    private final TextsAnalyzer textsAnalyzer;
//    private final DateAnalyzer dateAnalyzer;
//    private final FloatAnalyzer floatAnalyzer = new FloatAnalyzer();
//    private final DoubleAnalyzer doubleAnalyzer = new DoubleAnalyzer();
//    private final KeywordAnalyzer keywordAnalyzer = new KeywordAnalyzer();
//
//    private final ElasticsearchDbProvider elasticsearchClient = new ElasticsearchDbProvider();
//    private final ElasticsearchController elasticsearchController = new ElasticsearchController();
//    private final Map<String, Object> schema = new HashMap<>();
//
//    public MappingGeneratorCLI(final List<DateFormatsPOJO> dateFormats) {
//        this.textsAnalyzer = new TextsAnalyzer(dateFormats);
//        this.dateAnalyzer = new DateAnalyzer(dateFormats);
//    }
//
//    public boolean generate(final ProjectPOJO projectConfiguration) throws ClusterConnectionException, IOException {
//        AppInputPOJO appInput = projectConfiguration.getInputSettings();
//        AppOutputPOJO appOutput = projectConfiguration.getOutputSettings();
//        String projectName = projectConfiguration.getProjectName();
//        RestHighLevelClient client = elasticsearchClient.getHighLevelClient(projectConfiguration.getInputSettings().getEsSettings());
//        generateFromEs(appInput, client);
//        boolean result = true;
//        GeneralUtils.createFolderDeleteOldIfExists(IndexAnalyzerCLI.projectsFolder + projectName);
//        GeneralUtils.appendToFile(IndexAnalyzerCLI.projectsFolder + projectName + "/" + EAppSettings.ANALYZER_CHANGELOG_FILE,
//                "Log of changes for the project: " + projectName);
//        if (appOutput.isGenerateTemplate()) {
//            result = generateTemplate(projectConfiguration.getOutputSettings(), projectName);
//        }
//        if (appOutput.isGenerateIndex()) {
//            result = result && generateIndex(projectConfiguration.getOutputSettings(), projectName);
//        }
//        return result;
//    }
//
//    public boolean generateTemplate(final AppOutputPOJO outputSettings, String projectName) {
//        TemplateOutputPOJO templateOutput = new TemplateOutputPOJO();
//        templateOutput.setOrder(outputSettings.getAppTemplateProperties().getOrder());
//        templateOutput.setIndexPatterns(outputSettings.getAppTemplateProperties().getIndexPatterns());
//        templateOutput.setSettings(new TemplateSettingsPOJO(new IndexSettingsOutputPOJO(outputSettings.getAppIndexSettings())));
//        templateOutput.setMappings(schema.get("mappings"));
//        String templateFile = IndexAnalyzerCLI.projectsFolder + projectName + "/" + outputSettings.getAppTemplateProperties().getTemplateName() + ".json";
//        return GeneralUtils.saveJsonToToFileWithAdditionalFirstString(templateFile,
//                generateTemplateAPIRequest(outputSettings.getAppTemplateProperties().getTemplateName()),
//                templateOutput);
//    }
//
//    public boolean generateIndex(final AppOutputPOJO outputSettings, String projectName) {
//        IndexOutputPOJO indexOutput = new IndexOutputPOJO();
//        indexOutput.setSettings(new TemplateSettingsPOJO(new IndexSettingsOutputPOJO(outputSettings.getAppIndexSettings())));
//        indexOutput.setMappings(schema.get("mappings"));
//        String indexFileForTest = IndexAnalyzerCLI.projectsFolder + projectName + "/" + outputSettings.getAppIndexProperties().getIndexName() + ".json";
//        return GeneralUtils.saveJsonToToFileWithAdditionalFirstString(indexFileForTest,
//                generateIndexAPIRequest(outputSettings.getAppIndexProperties().getIndexName()),
//                indexOutput);
//    }
//
//    public boolean generateFromEs(final AppInputPOJO inputSettings, RestHighLevelClient client) throws IOException {
//        ScrollResultsPOJO searchResults = elasticsearchController.searchWithScrollFirstRequest(inputSettings, client);
//        long docsCount = 0;
//        while (searchResults.getResults() != null && searchResults.getResults().size() > 0 && inputSettings.getMaxDocsForAnalyze() > docsCount) {
//            searchResults.getResults().forEach(hit -> {
//                generate(hit.getSourceAsString());
//            });
//            searchResults = elasticsearchController.searchWithScrollWithScrollId(searchResults.getScrollId(), client);
//            if (logger.isDebugEnabled()) {
//                logger.debug("processed " + docsCount + " documents");
//            }
//            docsCount += 1000;
//            if (docsCount % 100000 == 0) {
//                logger.info("processed " + docsCount + " documents");
//            }
//        }
//        return elasticsearchController.closeScrollRequest(searchResults.getScrollId(), client);
//    }
//
//    public void generate(final String raw) {
//        ObjectMapper mapper = new ObjectMapper();
//        try {
//            JsonNode actualObj = mapper.readTree(raw);
//            updateSchema(schema, actualObj);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
//    }
//
//    //TODO add templates end point as configuration
//    //TODO check incorrect integer dimensions
//    private String generateTemplateAPIRequest(final String templateName) {
//        return "PUT _template/" + templateName;
//    }
//
//    private String generateIndexAPIRequest(final String indexName) {
//        return "PUT " + indexName;
//    }
//
//    private void updateSchema(Map<String, Object> schema, JsonNode actualObj) {
//        addToSchema("mappings", actualObj, schema);
//    }
//
//    //TODO add float
//    private void addToSchema(final String key, final JsonNode jsonNode, final Map<String, Object> schema) {
//        if (IndexAnalyzer.isDebugMode && key.equals(IndexAnalyzer.fieldForTest) && schema.get(key) != null) {
//            System.out.println("stop! Added to schema, old: " + ((FieldTypePropertiesPOJO) schema.get(key)).enumType() + " value:" + jsonNode.asText());
//        }
//        if (jsonNode.isObject()) {
//            ObjectNode objectNode = (ObjectNode) jsonNode;
//            HashMap<String, Object> childNode;
//            if (schema.containsKey(key)) {
//                childNode = (HashMap<String, Object>) schema.get(key);
//            } else {
//                childNode = new HashMap<>();
//                childNode.put("properties", new HashMap<String, Object>());
//                schema.put(key, childNode);
//            }
//
//
//            Iterator<Map.Entry<String, JsonNode>> iter = objectNode.fields();
//            while (iter.hasNext()) {
//                Map.Entry<String, JsonNode> entry = iter.next();
//                addToSchema(entry.getKey(), entry.getValue(), (HashMap<String, Object>) childNode.get("properties"));
//            }
//        } else if (jsonNode.isNull()) {
//            return;
//        } else if (jsonNode.isArray()) {
//            ArrayNode arrayNode = (ArrayNode) jsonNode;
//            if (arrayNode.size() > 0) {
//                boolean isObject = arrayNode.get(0).isObject() && arrayNode.get(0).size()>1;
////                int innerSize = arrayNode.get(0).size();
////                ((HashMap<String, Object>) ((ObjectNode) arrayNode.get(0))).size()
//                for (int i = 0; i < arrayNode.size(); i++) {
//                    addToSchema(key, arrayNode.get(i), schema);
//                }
//                if (isObject) {
//                    ((HashMap<String, Object>) schema.get(key)).put("type", "nested");
//                }
//            }
//
//        } else if (jsonNode.isValueNode()) {
//            ValueNode valueNode = (ValueNode) jsonNode;
////            if (key.equals("total_size_in_bytes") && valueNode.asLong() != 0) {
////                System.out.println("stop");
////
////            }
//            if (valueNode instanceof IntNode) {
//                if (valueNode.asInt() > 100000000 || valueNode.asInt() < -100000000) {
//                    if (!dateAnalyzer.isDateAndUpdate(key, schema, valueNode.longValue())) {
//                        longAnalyzer.analyze(key, schema);
//                    }
//                } else {
//                    integerAnalyzer.analyze(key, schema);
//                }
//            } else if (valueNode instanceof LongNode) {
//                if (!dateAnalyzer.isDateAndUpdate(key, schema, valueNode.longValue())) {
//                    longAnalyzer.analyze(key, schema);
//                }
//            } else if (valueNode instanceof BooleanNode) {
//                booleanAnalyzer.analyze(key, schema);
//            } else if (valueNode instanceof DoubleNode) {
//                doubleAnalyzer.analyze(key, schema);
//            } else if (valueNode instanceof FloatNode) {
//                floatAnalyzer.analyze(key, schema);
//            } else if (valueNode instanceof TextNode) {
//                textsAnalyzer.analyze(schema, jsonNode.textValue(), key);
//            } else {
//                keywordAnalyzer.analyze(key, schema);
//            }
//        }
////        System.out.println(schema);
//    }
//
//    private void readFileLineByLineAndUpdateSchema(final String file) {
//        Map<String, Object> schema = new HashMap<>();
//        ObjectMapper mapper = new ObjectMapper();
//        BufferedReader in = null;
//        Scanner read = null;
//        try {
//            in = new BufferedReader(new FileReader(file), 16 * 1024);
//            read = new Scanner(in);
//
//            while (read.hasNext()) {
//                String raw = read.next();
//                try {
//                    JsonNode actualObj = mapper.readTree(raw);
////                    updateSchema(schema, actualObj);
//                } catch (JsonProcessingException e) {
//                    e.printStackTrace();
//                }
//            }
//            System.out.println(schema);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } finally {
//            if (read != null) {
//                read.close();
//            }
//        }
//    }
//
//
//
//    protected String objectToString(Map<String, Object> object) throws JsonProcessingException {
//        mapper.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, false);
//        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
//    }
//
//    public Map<String, Object> getSchema() {
//        return schema;
//    }
//}
//private void readFileLineByLineAndUpdateSchema(final String file) {
//        Map<String, Object> schema = new HashMap<>();
//        ObjectMapper mapper = new ObjectMapper();
//        BufferedReader in = null;
//        Scanner read = null;
//        try {
//        in = new BufferedReader(new FileReader(file), 16 * 1024);
//        read = new Scanner(in);
//
//        while (read.hasNext()) {
//        String raw = read.next();
//        try {
//        JsonNode actualObj = mapper.readTree(raw);
//        } catch (JsonProcessingException e) {
//        e.printStackTrace();
//        }
//        }
//        System.out.println(schema);
//        } catch (FileNotFoundException e) {
//        e.printStackTrace();
//        } finally {
//        if (read != null) {
//        read.close();
//        }
//        }
//        }