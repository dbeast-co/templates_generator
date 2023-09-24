package com.dbeast.templates_generator.legacy_to_index_templates_converter;

import com.dbeast.templates_generator.templates_generator.pojo.EsSettingsPOJO;
import com.dbeast.templates_generator.utils.GeneralUtils;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.LinkedList;
import java.util.List;

public class TemplateConverterSettingsPOJO {
    @JsonProperty("project_id")
    private String projectId = GeneralUtils.generateNewUID();
    @JsonProperty("connection_settings")
    private EsSettingsPOJO connectionSettings = new EsSettingsPOJO();
    @JsonProperty("is_generate_dedicated_components_template")
    private boolean isGenerateDedicatedComponentsTemplate = false;

    @JsonProperty("is_separate_mappings_and_settings")
    private boolean isSeparateMappingsAndSettings = true;

    @JsonProperty("legacy_templates_list")
    private List<TemplateFromListPOJO> legacyTemplatesList = new LinkedList<>();

    @JsonProperty("is_generate_dedicated_components_template")
    public boolean isGenerateDedicatedComponentsTemplate() {
        return isGenerateDedicatedComponentsTemplate;
    }

    public void setGenerateDedicatedComponentsTemplate(boolean generateDedicatedComponentsTemplate) {
        isGenerateDedicatedComponentsTemplate = generateDedicatedComponentsTemplate;
    }

    @JsonProperty("is_separate_mappings_and_settings")
    public boolean isSeparateMappingsAndSettings() {
        return isSeparateMappingsAndSettings;
    }

    public void setSeparateMappingsAndSettings(boolean separateMappingsAndSettings) {
        isSeparateMappingsAndSettings = separateMappingsAndSettings;
    }

    public List<TemplateFromListPOJO> getLegacyTemplatesList() {
        return legacyTemplatesList;
    }

    public void setLegacyTemplatesList(List<TemplateFromListPOJO> legacyTemplatesList) {
        this.legacyTemplatesList = legacyTemplatesList;
    }

    public EsSettingsPOJO getConnectionSettings() {
        return connectionSettings;
    }

    public void setConnectionSettings(EsSettingsPOJO connectionSettings) {
        this.connectionSettings = connectionSettings;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
}
