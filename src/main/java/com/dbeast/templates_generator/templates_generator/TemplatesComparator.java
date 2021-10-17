package com.dbeast.templates_generator.templates_generator;

import com.dbeast.templates_generator.constants.EAppMessages;
import com.dbeast.templates_generator.constants.EFieldTypes;
import com.dbeast.templates_generator.constants.EProjectStatus;
import com.dbeast.templates_generator.elasticsearch.ElasticsearchController;
import com.dbeast.templates_generator.templates_generator.data_analizers.AbstractAnalyzer;
import com.dbeast.templates_generator.templates_generator.data_analizers.AnalyzerFactory;
import com.dbeast.templates_generator.templates_generator.pojo.DateFormatsPOJO;
import com.dbeast.templates_generator.templates_generator.pojo.EsSettingsPOJO;
import com.dbeast.templates_generator.templates_generator.pojo.fields_properties.FieldTypePropertiesPOJO;
import com.dbeast.templates_generator.templates_generator.pojo.ui_pojo.project_settings.ExistingTemplateActions;
import com.dbeast.templates_generator.templates_generator.pojo.ui_pojo.project_settings.ProjectPOJO;
import org.elasticsearch.client.indices.IndexTemplateMetadata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TemplatesComparator {
    private final ProjectPOJO project;
    private final List<String> changeLog;
    //    private final boolean[] conflict = {false};
    private boolean isIgnoreConflicts;
    private boolean isReplaceExistingFields;
    private boolean isAddNotExistingFields;
    private boolean isGenerateDedicatedTemplate;
    private final AnalyzerFactory analyzerFactory;


    public TemplatesComparator(final ProjectPOJO project,
                               final List<DateFormatsPOJO> dateFormats) {
        this.project = project;
        this.changeLog = project.getMappingChangesLog();
        this.analyzerFactory = new AnalyzerFactory(dateFormats, changeLog);
    }

    public Map<String, Object> compareAndAddFields(final Map<String, Object> generatedMapping,
                                                   final Map<String, Object> existingTemplateMapping,
                                                   final ExistingTemplateActions action) {
        this.isIgnoreConflicts = action.isIgnoreTypeConflicts();
        this.isReplaceExistingFields = action.isReplaceExistingFieldTypes();
        this.isAddNotExistingFields = action.isAddAllFields() || action.isAddFieldsFromAtLeastOneTimeUsedRootLevel();
        this.isGenerateDedicatedTemplate = action.isGenerateDedicatedNoneExistingFieldsTemplate();
        if (action.isAddFieldsFromAtLeastOneTimeUsedRootLevel()) {
            cleanUnusedRootFields(castMapToObject(generatedMapping.get("properties")),
                    castMapToObject(existingTemplateMapping.get("properties")));
        }
        return recursiveMappingCompareAndUpdate(generatedMapping, existingTemplateMapping);

    }

    public IndexTemplateMetadata getTemplate(final EsSettingsPOJO esSettings,
                                             final String templateName,
                                             final String projectId) {
        ElasticsearchController elasticsearchController = new ElasticsearchController();
        return elasticsearchController.getTemplateByName(esSettings, templateName, projectId);
    }

    public void cleanUnusedRootFields(final Map<String, Object> generatedMapping,
                                      final Map<String, Object> existingTemplateMapping) {
        existingTemplateMapping.entrySet()
                .removeIf(entry -> !generatedMapping.containsKey(entry.getKey()));
    }

    public void addDynamicMapping(final Map<String, Object> generatedMapping,
                                  final Map<String, Object> existingTemplateMapping) {
        if (existingTemplateMapping.containsKey("dynamic_templates")) {
            generatedMapping.put("dynamic_templates", existingTemplateMapping.get("dynamic_templates"));
        }
    }

    private Map<String, Object> recursiveMappingCompareAndUpdate(final Map<String, Object> generatedMapping,
                                                                 final Map<String, Object> existingTemplateMapping) {
        try {
            castMapToObject(existingTemplateMapping.get("properties"))
                    .forEach((key, value) -> {
                        compareFieldAndUpdate(generatedMapping, existingTemplateMapping, key, value);
                        if (generatedMapping.containsKey("properties") &&
                                castMapToObject(generatedMapping.get("properties")).containsKey(key) &&
                                castMapToObject(generatedMapping.get("properties")).get(key) instanceof Map &&
                                castMapToObject(castMapToObject(generatedMapping.get("properties")).get(key)).size() == 0) {
                            castMapToObject(generatedMapping.get("properties")).remove(key);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return generatedMapping;
    }

    private void compareFieldAndUpdate(final Map<String, Object> generatedMapping,
                                       final Map<String, Object> existingTemplateMapping,
                                       final String key,
                                       final Object value) {
        if (castMapToObject(generatedMapping.get("properties")).containsKey(key)) {
            if (!(castMapToObject(generatedMapping.get("properties")).get(key) instanceof FieldTypePropertiesPOJO)) {
                if (castMapToObject(castMapToObject(generatedMapping.get("properties")).get(key)).containsKey("properties") &&
                        !castMapToObject(castMapToObject(existingTemplateMapping.get("properties")).get(key)).containsKey("properties")) {
                    setConflictMessageAndUpdateProject(generatedMapping,
                            key,
                            value,
                            "Object",
                            castMapToObject(castMapToObject(existingTemplateMapping.get("properties")).get(key)).get("type").toString());
                } else if (!castMapToObject(castMapToObject(generatedMapping.get("properties")).get(key)).containsKey("properties") &&
                        castMapToObject(castMapToObject(existingTemplateMapping.get("properties")).get(key)).containsKey("properties")) {
                    setConflictMessageAndUpdateProject(generatedMapping,
                            key,
                            value,
                            ((FieldTypePropertiesPOJO) castMapToObject(castMapToObject(generatedMapping.get("properties")).get(key))).getType(),
                            "Object");
                } else if (castMapToObject(castMapToObject(generatedMapping.get("properties")).get(key)).containsKey("properties") &&
                        castMapToObject(castMapToObject(existingTemplateMapping.get("properties")).get(key)).containsKey("properties")){
                    recursiveMappingCompareAndUpdate(
                            castMapToObject(castMapToObject(generatedMapping.get("properties")).get(key)),
                            castMapToObject(castMapToObject(existingTemplateMapping.get("properties")).get(key)));
                    if (castMapToObject(generatedMapping.get("properties")).size() == 0) {
                        generatedMapping.remove("properties");
                    }
                } else {
                    String generatedTemplateType =  castMapToObject(castMapToObject(generatedMapping.get("properties")).get(key)).get("type").toString();
                    AbstractAnalyzer analyzer = analyzerFactory.getAnalyzerByFieldType(generatedTemplateType);
                    boolean isCompatible = analyzer.isConflictInField(EFieldTypes.getTypeByValue(castMapToObject(value).get("type").toString()));
                    if (!isCompatible) {
                        setConflictMessageAndUpdateProject(generatedMapping,
                                key,
                                value,
                                generatedTemplateType,
                                castMapToObject(value).get("type").toString()
                        );
                    } else {
                        if (isGenerateDedicatedTemplate) {
                            castMapToObject(generatedMapping.get("properties")).remove(key);
                        } else {
                            if (isReplaceExistingFields) {
                                castMapToObject(generatedMapping.get("properties")).put(key, value);
                            }
                        }
                    }
                }
            } else {
                if (value != null) {
                    if (castMapToObject(value).containsKey("properties")) {
                        setConflictMessageAndUpdateProject(generatedMapping,
                                key,
                                value,
                                ((FieldTypePropertiesPOJO) castMapToObject(generatedMapping.get("properties")).get(key)).getType(),
                                "Object");
                    } else {
                        String generatedTemplateType = ((FieldTypePropertiesPOJO) castMapToObject(generatedMapping.get("properties")).get(key)).getType();
                        AbstractAnalyzer analyzer = analyzerFactory.getAnalyzerByFieldType(generatedTemplateType);
                        boolean isCompatible = analyzer.isConflictInField(EFieldTypes.getTypeByValue(castMapToObject(value).get("type").toString()));
                        if (!isCompatible) {
                            setConflictMessageAndUpdateProject(generatedMapping,
                                    key,
                                    value,
                                    generatedTemplateType,
                                    castMapToObject(value).get("type").toString()
                            );
                        } else {
                            if (isGenerateDedicatedTemplate) {
                                castMapToObject(generatedMapping.get("properties")).remove(key);
                            } else {
                                if (isReplaceExistingFields) {
                                    castMapToObject(generatedMapping.get("properties")).put(key, value);
                                }
                            }
                        }
                    }
                }
                if (castMapToObject(generatedMapping.get("properties")).size() == 0) {
                    generatedMapping.remove("properties");
                }
            }
        } else {
            if (isAddNotExistingFields) {
                castMapToObject(generatedMapping.get("properties")).put(key, value);
            }
        }
    }

    private Map<String, Object> castMapToObject(Object sourceObject) {
        try {
            return (Map<String, Object>) sourceObject;
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    private void setConflictMessageAndUpdateProject(Map<String, Object> generatedMapping,
                                                    String key,
                                                    Object value,
                                                    String generatedType,
                                                    String existingType) {
        changeLog.add(String.format(EAppMessages.ON_TYPE_CONFLICT_LOG_MESSAGE.getMessage(), key,
                generatedType,
                existingType
        ));
        if (!isIgnoreConflicts) {
            project.getStatus().setProjectStatus(EProjectStatus.FAILED);
            project.getStatus().setFailed(true);
        } else {
            if (isGenerateDedicatedTemplate) {
                castMapToObject(generatedMapping.get("properties")).remove(key);
            } else {
                if (isReplaceExistingFields) {
                    castMapToObject(generatedMapping.get("properties")).put(key, value);
                }
            }
        }
    }

}
