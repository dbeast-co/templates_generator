package com.dbeast.templates_generator.legacy_to_index_templates_converter;

import com.dbeast.templates_generator.TemplatesGenerator;
import com.dbeast.templates_generator.constants.EAppSettings;
import com.dbeast.templates_generator.data_warehouse.MappingGeneratorController;
import com.dbeast.templates_generator.elasticsearch.ElasticsearchController;
import com.dbeast.templates_generator.exceptions.ClusterConnectionException;
import com.dbeast.templates_generator.templates_generator.pojo.ui_pojo.project_output.ComponentTemplateOutputPOJO;
import com.dbeast.templates_generator.templates_generator.pojo.ui_pojo.project_output.IndexTemplateOutputPOJO;
import com.dbeast.templates_generator.templates_generator.pojo.ui_pojo.project_output.TemplateOutputPOJO;
import com.dbeast.templates_generator.templates_generator.pojo.ui_pojo.project_settings.TemplatePropertiesPOJO;
import com.dbeast.templates_generator.utils.GeneralUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import spark.Response;

import javax.servlet.ServletOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.*;
import java.util.stream.Collectors;

public class LegacyToIndexTemplatesConverter {
    private static final LegacyToIndexTemplatesConverter _instance = new LegacyToIndexTemplatesConverter();


    public static LegacyToIndexTemplatesConverter getInstance() {
        if (_instance == null) {
            return new LegacyToIndexTemplatesConverter();
        }
        return _instance;
    }

    ElasticsearchController elasticsearchController = new ElasticsearchController();


    public void convertToComponentTemplate(final String legacyTemplate,
                                           final boolean generateDedicatedComponentsTemplate,
                                           final boolean separateMappingsAndSettings,
                                           final String projectId) throws JsonProcessingException {
        boolean generationResult = true;
        ObjectMapper jsonMapper = new ObjectMapper();
        Map<String, Map<String, Object>> jsonTemplate = jsonMapper.readValue(legacyTemplate, new TypeReference<Map<String, Map<String, Object>>>() {
        });
        String templateName = jsonTemplate.keySet().stream().findFirst().orElse(null);
        Map<String, Object> template = jsonTemplate.get(templateName);
        Map<String, Map<String, Object>> settings = jsonMapper.convertValue(template.get("settings"), new TypeReference<Map<String, Map<String, Object>>>() {
        });
        Map<String, Object> mappings = jsonMapper.convertValue(template.get("mappings"), new TypeReference<Map<String, Object>>() {
        });
        Map<String, Object> aliases = jsonMapper.convertValue(template.get("aliases"), new TypeReference<Map<String, Object>>() {
        });
        List<String> indexPatterns = (List<String>) template.get("index_patterns");
        int order = (int) template.get("order");
        List<String> componentsList = new LinkedList<>();

        if (generateDedicatedComponentsTemplate) {
            if (separateMappingsAndSettings) {
                generationResult = generationResult && generateComponentTemplate(
                        templateName,
                        projectId,
                        mappings,
                        new HashMap<>(),
                        "-mapping-component");
                //Generate settings component
                generationResult = generationResult && generateComponentTemplate(
                        templateName,
                        projectId,
                        new HashMap<>(),
                        settings,
                        "-settings-component");
                componentsList.add(templateName + "-mapping-component");
                componentsList.add(templateName + "-settings-component");
            } else {
                // Generate all in one component
                generationResult = generationResult && generateComponentTemplate(
                        templateName,
                        projectId,
                        mappings,
                        settings,
                        "-component");
                componentsList.add(templateName + "-component");
            }
            generationResult = generationResult && generateIndexTemplate(
                    templateName,
                    order,
                    projectId,
                    indexPatterns,
                    new HashMap<>(),
                    new HashMap<>(),
                    componentsList,
                    aliases);
        } else {
            generateIndexTemplate(templateName,
                    order,
                    projectId,
                    indexPatterns,
                    mappings,
                    settings,
                    new LinkedList<>(),
                    aliases);
        }
        try {
            GeneralUtils.zipDirectory(TemplatesGenerator.projectsFolder + projectId,
                    TemplatesGenerator.projectsFolder + projectId + "/" + EAppSettings.ANALYZER_TEMPLATE_ZIP_FILE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Response runProject(Response response,
                               TemplateConverterSettingsPOJO project){
        GeneralUtils.createFolderIfNotExists(TemplatesGenerator.projectsFolder + project.getProjectId());
        project.getLegacyTemplatesList().stream()
                .filter(TemplateFromListPOJO::isIs_checked)
                .forEach(template -> {
                    String legacyTemplate = null;
                    try {
                        legacyTemplate = elasticsearchController.getTemplateParameters(
                                project.getConnectionSettings(),
                                template.getTemplateName(),
                                project.getProjectId());
                        convertToComponentTemplate(legacyTemplate,
                                project.isGenerateDedicatedComponentsTemplate(),
                                project.isSeparateMappingsAndSettings(),
                                project.getProjectId());
                    } catch (JsonProcessingException | ClusterConnectionException e) {
                        throw new RuntimeException(e);
                    }
                });
//        try {

//            getFile(response, project.getProjectId());
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
        return response;
    }

    public void getFile(Response response, String projectId) throws IOException {
        String fileName = "templates.zip";

        File file = new File(TemplatesGenerator.projectsFolder + projectId + "/"
                + fileName);

        response.raw().setContentType("application/octet-stream");
        response.raw().setHeader("Content-Disposition", "attachment; filename=" + file.getName());

        response.raw().setContentLength((int) file.length());
        response.status(200);

        ServletOutputStream os = response.raw().getOutputStream();
        try {
            os.write(Files.readAllBytes(file.toPath()));
        } catch (NoSuchFileException e) {
            os.write(new byte[0]);
        }
        os.flush();
        os.close();
//        return response;
    }

    public void getTemplatesList(TemplateConverterSettingsPOJO project) {
        Set<String> templates = elasticsearchController.getLegacyTemplateList(project.getConnectionSettings(), project.getProjectId());
        List<TemplateFromListPOJO> templateList = templates.stream()
                .map(TemplateFromListPOJO::new)
                .collect(Collectors.toList());
        project.setLegacyTemplatesList(templateList);
    }

    private boolean generateIndexTemplate(final String templateName,
                                          final int order,
                                          final String projectId,
                                          final List<String> indexPatterns,
                                          final Map<String, Object> templateMapping,
                                          final Map<String, Map<String, Object>> indexSettings,
                                          final List<String> componentsList,
                                          final Map<String, Object> aliases) {
        IndexTemplateOutputPOJO templateOutput = new IndexTemplateOutputPOJO();
        templateOutput.setPriority(order);
        templateOutput.setIndexPatterns(indexPatterns);
        TemplateOutputPOJO template = new TemplateOutputPOJO();
        if (indexSettings.size() > 0) {
            template.setSettings(indexSettings);
        }
        if (templateMapping.size() > 0) {
            template.setMappings(templateMapping);
        }
        template.setAliases(aliases);
        templateOutput.setTemplate(template);
        templateOutput.setComposed_of(componentsList);

        String templateFile = TemplatesGenerator.projectsFolder + projectId + "/" + templateName + "-index-template.json";
        return GeneralUtils.saveJsonToToFileWithAdditionalFirstString(templateFile,
                generateIndexTemplateAPIRequest(templateName),
                templateOutput);
    }

    private boolean generateComponentTemplate(final String templateName,
                                              final String projectId,
                                              final Map<String, Object> templateMapping,
                                              final Map<String, Map<String, Object>> indexSettings,
                                              final String fileNameSuffix) {
        ComponentTemplateOutputPOJO templateOutput = new ComponentTemplateOutputPOJO();
        TemplateOutputPOJO template = new TemplateOutputPOJO();
        if (indexSettings.size() > 0) {
            template.setSettings(indexSettings);
        }
        if (templateMapping.size() > 0) {
            template.setMappings(templateMapping);
        }
        templateOutput.setTemplate(template);
        String templateFile = TemplatesGenerator.projectsFolder + projectId + "/" + templateName + fileNameSuffix + ".json";
        return GeneralUtils.saveJsonToToFileWithAdditionalFirstString(templateFile,
                generateComponentTemplateAPIRequest(templateName + fileNameSuffix),
                templateOutput);
    }


    private String generateComponentTemplateAPIRequest(final String templateName) {
        return "PUT _component_template/" + templateName;
    }

    private String generateIndexTemplateAPIRequest(final String templateName) {
        return "PUT _index_template/" + templateName;
    }
}
