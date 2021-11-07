package com.dbeast.templates_generator.templates_generator;

import com.dbeast.templates_generator.TemplatesGenerator;
import com.dbeast.templates_generator.constants.EAppSettings;
import com.dbeast.templates_generator.constants.EFieldTypes;
import com.dbeast.templates_generator.constants.EProjectStatus;
import com.dbeast.templates_generator.elasticsearch.ElasticsearchController;
import com.dbeast.templates_generator.elasticsearch.ElasticsearchDbProvider;
import com.dbeast.templates_generator.exceptions.ClusterConnectionException;
import com.dbeast.templates_generator.exceptions.IndexNotFoundOrEmptyException;
import com.dbeast.templates_generator.exceptions.TemplateNotFoundException;
import com.dbeast.templates_generator.templates_generator.data_analizers.*;
import com.dbeast.templates_generator.templates_generator.pojo.DateFormatsPOJO;
import com.dbeast.templates_generator.templates_generator.pojo.fields_properties.FieldTypePropertiesPOJO;
import com.dbeast.templates_generator.templates_generator.pojo.fields_properties.IgnoreAbovePropertiesPOJO;
import com.dbeast.templates_generator.templates_generator.pojo.fields_properties.TextAndKeywordPropertiesPOJO;
import com.dbeast.templates_generator.templates_generator.pojo.ui_pojo.project_output.IndexOutputPOJO;
import com.dbeast.templates_generator.templates_generator.pojo.ui_pojo.project_output.NormalizerPOJO;
import com.dbeast.templates_generator.templates_generator.pojo.ui_pojo.project_output.TemplateOutputPOJO;
import com.dbeast.templates_generator.templates_generator.pojo.ui_pojo.project_settings.*;
import com.dbeast.templates_generator.utils.GeneralUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.IndexTemplateMetadata;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

//TODO change to configuration file from statics
public class MappingGenerator {
    private static final Logger logger = LogManager.getLogger();

    private final IntegerAnalyzer integerAnalyzer;
    private final LongAnalyzer longAnalyzer;
    private final BooleanAnalyzer booleanAnalyzer;
    private final TextAnalyzer textAnalyzer;
    private final DateAnalyzer dateAnalyzer;
    private final FloatAnalyzer floatAnalyzer;
    private final DoubleAnalyzer doubleAnalyzer;
    private final KeywordAnalyzer keywordAnalyzer;

    private final ElasticsearchDbProvider elasticsearchClient = new ElasticsearchDbProvider();
    private final ElasticsearchController elasticsearchController = new ElasticsearchController();
    private final ProjectPOJO project;
    private final TemplatesComparator templatesComparator;

    public MappingGenerator(final List<DateFormatsPOJO> dateFormats,
                            final ProjectPOJO project) {
        this.project = project;
//        boolean isStopped = project.getStatus().isStopped();
        this.textAnalyzer = new TextAnalyzer(dateFormats, project.getMappingChangesLog());
        this.dateAnalyzer = new DateAnalyzer(dateFormats, project.getMappingChangesLog());
        this.booleanAnalyzer = new BooleanAnalyzer(project.getMappingChangesLog());
        this.longAnalyzer = new LongAnalyzer(project.getMappingChangesLog());
        this.integerAnalyzer = new IntegerAnalyzer(project.getMappingChangesLog());
        this.floatAnalyzer = new FloatAnalyzer(project.getMappingChangesLog());
        this.doubleAnalyzer = new DoubleAnalyzer(project.getMappingChangesLog());
        this.keywordAnalyzer = new KeywordAnalyzer(project.getMappingChangesLog());
        this.templatesComparator = new TemplatesComparator(project, dateFormats);
    }

    public void generate(final String raw,
                         final Map<String, Object> generatedMapping) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode actualObj = mapper.readTree(raw);
            updateSchema(generatedMapping, actualObj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }


    public boolean generate() throws ClusterConnectionException, IOException, IndexNotFoundOrEmptyException, TemplateNotFoundException {
        Map<String, Object> templateGeneratedMapping = new HashMap<>();

        project.getStatus().insertStartTimeInLong(System.currentTimeMillis());
        project.getStatus().setInActiveProcess(true);
        project.getStatus().setProjectStatus(EProjectStatus.ON_FLY);

        InputSettingsPOJO appInput = project.getInputSettings().getInputSettings();
        String projectId = project.getProjectId();
        RestHighLevelClient client = elasticsearchClient.getHighLevelClient(project.getInputSettings().getSourceCluster());

        elasticsearchController.getClusterStatus(client);

        if (project.getOutputSettings().getTemplateProperties().isAddFieldsFromExistingTemplate()) {
            throwExceptionIfTemplateNotExists(client, project.getOutputSettings().getTemplateProperties().getExistingTemplateActions().getAddFieldsFromExistingTemplateName());
        }
        if (project.getOutputSettings().getIndexProperties().isAddFieldsFromExistingTemplate()) {
            throwExceptionIfTemplateNotExists(client, project.getOutputSettings().getIndexProperties().getExistingTemplateActions().getAddFieldsFromExistingTemplateName());
        }

        long docsInSource = elasticsearchController.getDocsCount(client,
                project.getInputSettings().getInputSettings().getIndexForAnalyze());

        if (docsInSource > 0) {
            long docsForAnalyze = Math.min(docsInSource, project.getInputSettings().getInputSettings().getMaxDocsForAnalyze());
            project.getStatus().setDocsInIndex(docsInSource);
            project.getStatus().setDocsForAnalyze(docsForAnalyze);

            boolean generationResult = generateFromEs(appInput, client, docsForAnalyze, templateGeneratedMapping);
            if (!generationResult) {
                project.getStatus().setFailed(true);
            } else {
                project.getStatus().setDone(true);
                GeneralUtils.appendToFile(TemplatesGenerator.projectsFolder + projectId + "/" +
                                EAppSettings.ANALYZER_CHANGELOG_FILE.getConfigurationParameter(),
                        "Log of changes for the project: " + project.getProjectName());
                generationResult = generateIndexAndTemplate(templateGeneratedMapping,
                        projectId,
                        client,
                        generationResult);
                project.getMappingChangesLog().forEach(log ->
                        GeneralUtils.appendTextToFile(TemplatesGenerator.projectsFolder + projectId + "/" +
                                EAppSettings.ANALYZER_CHANGELOG_FILE.getConfigurationParameter(), log)
                );
            }
            project.getStatus().setInActiveProcess(false);
            project.getStatus().setDone(true);
            if (generationResult) {
                project.getStatus().setProjectStatus(EProjectStatus.SUCCEEDED);
            } else {
                project.getStatus().setProjectStatus(EProjectStatus.FAILED);
            }
            project.getStatus().insertEndTimeInLong(System.currentTimeMillis());
            GeneralUtils.createFile(TemplatesGenerator.projectsFolder + projectId + "/" +
                    EAppSettings.PROJECT_SETTINGS_FILE.getConfigurationParameter(), project);
            GeneralUtils.zipDirectory(TemplatesGenerator.projectsFolder + projectId,
                    TemplatesGenerator.projectsFolder + projectId + "/" +
                            EAppSettings.ANALYZER_ALL_LOGS_FILE.getConfigurationParameter());
            return generationResult;
        } else {
            throw new IndexNotFoundOrEmptyException(project.getInputSettings().getInputSettings().getIndexForAnalyze());
        }
    }

    public void updateFailStatus() {
        project.getStatus().setInActiveProcess(false);
        project.getStatus().setProjectStatus(EProjectStatus.FAILED);
        project.getStatus().setDone(true);
        project.getStatus().setFailed(true);
        project.getStatus().insertEndTimeInLong(System.currentTimeMillis());
    }

    private void addNormalizerToAllKeywordFields(Map<String, Object> templateGeneratedMapping) {
        for (Map.Entry entry :
                templateGeneratedMapping.entrySet()) {
            if (entry.getValue() instanceof Map) {
                addNormalizerToAllKeywordFields(castMapToObject(templateGeneratedMapping.get(entry.getKey())));
            } else if (entry.getValue() instanceof IgnoreAbovePropertiesPOJO) {
                ((IgnoreAbovePropertiesPOJO) entry.getValue()).setNormalizer();
            } else if (entry.getValue() instanceof TextAndKeywordPropertiesPOJO) {
                ((TextAndKeywordPropertiesPOJO) entry.getValue()).addNormalizer();
            } else if (entry.getKey().equals("type") && entry.getValue().toString().equals("keyword")) {
                templateGeneratedMapping.put("normalizer", EAppSettings.DEFAULT_NORMALIZER_NAME.getConfigurationParameter());
            }
        }
    }

    private void throwExceptionIfTemplateNotExists(RestHighLevelClient client, String templateName) throws TemplateNotFoundException {
        boolean isTemplateExists = elasticsearchController.isTemplateExists(client, templateName);
        if (!isTemplateExists) {
            updateFailStatus();
            throw new TemplateNotFoundException(templateName);
        }
    }

    private boolean generateIndexAndTemplate(final Map<String, Object> templateGeneratedMapping,
                                             final String projectId,
                                             final RestHighLevelClient client,
                                             boolean generationResult) {
        Map<String, Object> indexGeneratedMapping = cloneMapping(templateGeneratedMapping);
        if (project.getActions().isGenerateTemplate()) {
            Map<String, Map<String, Object>> indexSettings = project.getOutputSettings().getTemplateProperties().getIndexSettings().settingsAsMap(new HashMap<>());
            if (project.getOutputSettings().getTemplateProperties().isAddFieldsFromExistingTemplate() &&
                    project.getOutputSettings().getTemplateProperties().getExistingTemplateActions().getAddFieldsFromExistingTemplateName() != null &&
                    !project.getOutputSettings().getTemplateProperties().getExistingTemplateActions().getAddFieldsFromExistingTemplateName().isEmpty()) {
                compareToExistingTemplateAndUpdate(client,
                        true,
                        castMapToObject(templateGeneratedMapping.get("mappings")),
                        indexSettings);
            }
            if (project.getOutputSettings().getTemplateProperties().isAddNormalizerToKeywordFields()) {
                indexSettings.put("analysis", new HashMap<String, Object>() {{
                    put("normalizer",
                            new HashMap<String, Object>() {{
                                put(EAppSettings.DEFAULT_NORMALIZER_NAME.getConfigurationParameter(), new NormalizerPOJO());
                            }});
                }});
                addNormalizerToAllKeywordFields(templateGeneratedMapping);
            }
            generationResult = generateTemplate(project.getOutputSettings().getTemplateProperties(), projectId,
                    castMapToObject(templateGeneratedMapping.get("mappings")),
                    indexSettings);
        }

        if (project.getActions().isGenerateIndex()) {

            Map<String, Map<String, Object>> indexSettings = project.getOutputSettings().getIndexProperties().getIndexSettings().settingsAsMap(new HashMap<>());
            if (project.getOutputSettings().getIndexProperties().isAddFieldsFromExistingTemplate() &&
                    project.getOutputSettings().getIndexProperties().getExistingTemplateActions().getAddFieldsFromExistingTemplateName() != null &&
                    !project.getOutputSettings().getIndexProperties().getExistingTemplateActions().getAddFieldsFromExistingTemplateName().isEmpty()) {
                compareToExistingTemplateAndUpdate(client,
                        false,
                        castMapToObject(indexGeneratedMapping.get("mappings")),
                        indexSettings);

            }
            if (project.getOutputSettings().getIndexProperties().isAddNormalizerToKeywordFields()) {
                indexSettings.put("analysis", new HashMap<String, Object>() {{
                    put("normalizer",
                            new HashMap<String, Object>() {{
                                put(EAppSettings.DEFAULT_NORMALIZER_NAME.getConfigurationParameter(), new NormalizerPOJO());
                            }});
                }});
                addNormalizerToAllKeywordFields(templateGeneratedMapping);
            }
            generationResult = generationResult && generateIndex(project.getOutputSettings().getIndexProperties(), projectId,
                    castMapToObject(indexGeneratedMapping.get("mappings")),
                    indexSettings);
        }
        return generationResult;
    }

    private void compareToExistingTemplateAndUpdate(final RestHighLevelClient client,
                                                    final boolean isTemplate,
                                                    final Map<String, Object> generatedMapping,
                                                    Map<String, Map<String, Object>> indexSettings) {
        ExistingTemplateActions existingTemplateActions;
        if (isTemplate) {
            existingTemplateActions = project.getOutputSettings().getTemplateProperties().getExistingTemplateActions();
            indexSettings = project.getOutputSettings().getTemplateProperties().getIndexSettings().settingsAsMap(indexSettings);
        } else {
            existingTemplateActions = project.getOutputSettings().getIndexProperties().getExistingTemplateActions();
            indexSettings = project.getOutputSettings().getTemplateProperties().getIndexSettings().settingsAsMap(indexSettings);
        }
        IndexTemplateMetadata existingTemplate = elasticsearchController.getTemplateByName(
                client, existingTemplateActions.getAddFieldsFromExistingTemplateName());
        Map<String, Object> newExistingTemplate = new HashMap<>();
        newExistingTemplate.put("properties", existingTemplate.mappings().getSourceAsMap().get("properties"));
        templatesComparator.compareAndAddFields(generatedMapping,
                newExistingTemplate,
                existingTemplateActions);
        if (existingTemplateActions.isAddDynamicMapping()) {
            templatesComparator.addDynamicMapping(generatedMapping, existingTemplate.mappings().getSourceAsMap());
        }
        if (existingTemplateActions.isAddSettings()) {
            if (existingTemplate.settings().getAsGroups().containsKey("index")) {
                String stringSettings = existingTemplate.settings().getAsGroups().get("index").toString();
                Map<String, Object> settingsFromExistingTemplate = new HashMap<>();
                ObjectMapper jsonMapper = new ObjectMapper();
                try {
                    settingsFromExistingTemplate = jsonMapper.readValue(stringSettings, new TypeReference<Map<String, Object>>() {
                    });
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                //TODO COMPARE TO GENERATED
                compareSettingsAndUpdate(indexSettings.get("index"), settingsFromExistingTemplate);
            }
        }
    }

    private void compareSettingsAndUpdate(final Map<String, Object> indexSettings,
                                          final Map<String, Object> settingsFromExistingTemplate) {
        settingsFromExistingTemplate.entrySet()
                .stream().filter(entry -> !indexSettings.containsKey(entry.getKey()))
                .forEach(entry -> indexSettings.put(entry.getKey(), entry.getValue()));
    }

    private boolean generateTemplate(final TemplatePropertiesPOJO outputSettings,
                                     final String projectName,
                                     final Map<String, Object> templateMapping,
                                     final Map<String, Map<String, Object>> indexSettings) {
        TemplateOutputPOJO templateOutput = new TemplateOutputPOJO();
        templateOutput.setOrder(outputSettings.getOrder());
        List<String> indexPatterns = outputSettings.generateIndexPatternsForTemplate();
        templateOutput.setIndexPatterns(indexPatterns);
        templateOutput.setSettings(indexSettings);
        templateOutput.setMappings(templateMapping);
        if (outputSettings.getAlias() != null && !outputSettings.getAlias().isEmpty()) {
            Map<String, Map<String, String>> alias = new HashMap<>();
            alias.put(outputSettings.getAlias(), new HashMap<>());
            templateOutput.setAliases(alias);
        }
        String templateFile = TemplatesGenerator.projectsFolder + projectName + "/" + outputSettings.getTemplateName() + ".json";
        return GeneralUtils.saveJsonToToFileWithAdditionalFirstString(templateFile,
                generateTemplateAPIRequest(outputSettings.getTemplateName()),
                templateOutput);
    }

    private boolean generateIndex(final IndexPropertiesPOJO outputSettings,
                                  final String projectName,
                                  final Map<String, Object> indexMapping,
                                  final Map<String, Map<String, Object>> indexSettings) {
        IndexOutputPOJO indexOutput = new IndexOutputPOJO();
        indexOutput.setSettings(indexSettings);
        indexOutput.setMappings(indexMapping);
        String indexFileForTest = TemplatesGenerator.projectsFolder + projectName + "/" + outputSettings.getIndexName() + ".json";
        if (outputSettings.getAlias() != null && !outputSettings.getAlias().isEmpty()) {
            Map<String, Map<String, String>> alias = new HashMap<>();
            alias.put(outputSettings.getAlias(), new HashMap<>());
            indexOutput.setAliases(alias);
        }
        return GeneralUtils.saveJsonToToFileWithAdditionalFirstString(indexFileForTest,
                generateIndexAPIRequest(outputSettings.getIndexName()),
                indexOutput);
    }

    private boolean generateFromEs(final InputSettingsPOJO inputSettings,
                                   final RestHighLevelClient client,
                                   long docsForAnalyze,
                                   final Map<String, Object> generatedMapping) throws IOException {
        ScrollResultsPOJO searchResults = elasticsearchController.searchWithScrollFirstRequest(inputSettings, client);
        long docsCount = 0;
        if (project.getInputSettings().getInputSettings().getScrollSize() > docsForAnalyze) {
            project.getInputSettings().getInputSettings().setScrollSize(Math.toIntExact(docsForAnalyze));
        }

        while (!project.getStatus().isStopped() && searchResults.getResults() != null &&
                searchResults.getResults().size() > 0 &&
                docsForAnalyze > docsCount) {
            searchResults.getResults().forEach(hit -> generate(hit.getSourceAsString(), generatedMapping));

            docsCount += searchResults.getResults().size();
            project.getStatus().setAnalyzedDocs(docsCount);
            project.getStatus().setExecutionProgress();
            GeneralUtils.createFile(TemplatesGenerator.projectsFolder + project.getProjectId() + "/" +
                    EAppSettings.PROJECT_SETTINGS_FILE.getConfigurationParameter(), project);

            searchResults = elasticsearchController.searchWithScrollWithScrollId(searchResults.getScrollId(), client);
            if (logger.isDebugEnabled() && (docsCount % 1000 == 0)) {
                logger.debug("processed " + docsCount + " documents");
            }
        }
        return elasticsearchController.closeScrollRequest(searchResults.getScrollId(), client);
    }


    private String generateTemplateAPIRequest(final String templateName) {
        return "PUT _template/" + templateName;
    }

    private String generateIndexAPIRequest(final String indexName) {
        return "PUT " + indexName;
    }

    private void updateSchema(Map<String, Object> schema,
                              JsonNode actualObj) {
        addToSchema("mappings", actualObj, schema);
    }

    //TODO add float
    private void addToSchema(final String key,
                             final JsonNode jsonNode,
                             final Map<String, Object> schema) {
        if (jsonNode.isObject()) {
            ObjectNode objectNode = (ObjectNode) jsonNode;
            HashMap<String, Object> childNode;
            if (schema.containsKey(key)) {
                childNode = castMapToObject(schema.get(key));
            } else {
                childNode = new HashMap<>();
                childNode.put("properties", new HashMap<String, Object>());
                schema.put(key, childNode);
            }
            Iterator<Map.Entry<String, JsonNode>> iter = objectNode.fields();
            while (iter.hasNext()) {
                Map.Entry<String, JsonNode> entry = iter.next();
                addToSchema(entry.getKey(), entry.getValue(), castMapToObject(childNode.get("properties")));
            }
        } else if (jsonNode.isNull()) {
        } else if (jsonNode.isArray()) {
            ArrayNode arrayNode = (ArrayNode) jsonNode;
            if (arrayNode.size() > 0) {
                boolean isObject = arrayNode.get(0).isObject() && arrayNode.size() > 1;
                for (int i = 0; i < arrayNode.size(); i++) {
                    addToSchema(key, arrayNode.get(i), schema);
                }
                if (isObject) {
                    castMapToObject(schema.get(key)).put("type", "nested");
                }
            }
        } else if (jsonNode.isValueNode()) {
            ValueNode valueNode = (ValueNode) jsonNode;
            if (valueNode instanceof IntNode) {
                if (valueNode.asInt() > 100000000 || valueNode.asInt() < -100000000) {
                    if (!dateAnalyzer.isDateAndUpdate(key, schema, valueNode.longValue())) {
                        longAnalyzer.analyze(key, schema);
                    }
                } else {
                    integerAnalyzer.analyze(key, schema);
                }
            } else if (valueNode instanceof LongNode) {
                if (!dateAnalyzer.isDateAndUpdate(key, schema, valueNode.longValue())) {
                    longAnalyzer.analyze(key, schema);
                }
            } else if (valueNode instanceof BooleanNode) {
                booleanAnalyzer.analyze(key, schema);
            } else if (valueNode instanceof DoubleNode) {
                if (!dateAnalyzer.isDateAndUpdateForDouble(key, schema, valueNode.longValue())) {
                    doubleAnalyzer.analyze(key, schema);
                }
            } else if (valueNode instanceof FloatNode) {
                floatAnalyzer.analyze(key, schema);
            } else if (valueNode instanceof TextNode) {
                textAnalyzer.analyze(schema, jsonNode.textValue(), key);
            } else {
                keywordAnalyzer.analyze(key, schema);
            }
        }
    }

    private Map<String, Object> cloneMapping(final Map<String, Object> originalMapping) {
        Map<String, Object> clonedMapping = new HashMap<>();
        recursiveCloneMapping(originalMapping, clonedMapping);
        return clonedMapping;
    }

    private void recursiveCloneMapping(final Map<String, Object> originalMapping,
                                       final Map<String, Object> clonedMapping) {
        originalMapping.forEach((key, value) -> {
            if (value instanceof Map) {
                clonedMapping.put(key, new HashMap<>());
                recursiveCloneMapping(castMapToObject(value),
                        castMapToObject(clonedMapping.get(key)));
            } else if (value instanceof FieldTypePropertiesPOJO) {
                try {
                    clonedMapping.put(key,
                            value.getClass().getConstructor(EFieldTypes.class)
                                    .newInstance(((FieldTypePropertiesPOJO) value).enumType()));
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
            } else {
                clonedMapping.put(key, value);
            }
        });
    }

    private HashMap<String, Object> castMapToObject(final Object sourceObject) {
        try {
            return (HashMap<String, Object>) sourceObject;
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }
}
