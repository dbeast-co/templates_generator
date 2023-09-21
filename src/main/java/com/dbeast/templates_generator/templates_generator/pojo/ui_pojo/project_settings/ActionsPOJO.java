package com.dbeast.templates_generator.templates_generator.pojo.ui_pojo.project_settings;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ActionsPOJO implements Cloneable{
    @JsonProperty("is_generate_template")
    private boolean isGenerateTemplate = false;

    @JsonProperty("is_generate_index_template")
    private boolean isGenerateIndexTemplate = true;
    @JsonProperty("is_generate_legacy_template")
    private boolean isGenerateLegacyTemplate = false;
    @JsonProperty("is_generate_dedicated_components_template")
    private boolean isGenerateDedicatedComponentsTemplate = false;

    @JsonProperty("is_separate_mappings_and_settings")
    private boolean isSeparateMappingsAndSettings = true;
    @JsonProperty("is_generate_index")
    private boolean isGenerateIndex = false;
    @Override
    public ActionsPOJO clone() {
        try {
            return (ActionsPOJO) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return this;
        }
    }
    @JsonProperty("is_generate_template")
    public boolean isGenerateTemplate() {
        return isGenerateTemplate;
    }

    public void setGenerateTemplate(boolean generateTemplate) {
        isGenerateTemplate = generateTemplate;
    }

    @JsonProperty("is_generate_index")
    public boolean isGenerateIndex() {
        return isGenerateIndex;
    }

    public void setGenerateIndex(boolean generateIndex) {
        isGenerateIndex = generateIndex;
    }

    public boolean isGenerateIndexTemplate() {
        return isGenerateIndexTemplate;
    }

    public void setGenerateIndexTemplate(boolean generateIndexTemplate) {
        isGenerateIndexTemplate = generateIndexTemplate;
    }

    public boolean isGenerateLegacyTemplate() {
        return isGenerateLegacyTemplate;
    }

    public void setGenerateLegacyTemplate(boolean generateLegacyTemplate) {
        isGenerateLegacyTemplate = generateLegacyTemplate;
    }

    public boolean isGenerateDedicatedComponentsTemplate() {
        return isGenerateDedicatedComponentsTemplate;
    }

    public void setGenerateDedicatedComponentsTemplate(boolean generateDedicatedComponentsTemplate) {
        isGenerateDedicatedComponentsTemplate = generateDedicatedComponentsTemplate;
    }

    public boolean isSeparateMappingsAndSettings() {
        return isSeparateMappingsAndSettings;
    }

    public void setSeparateMappingsAndSettings(boolean separateMappingsAndSettings) {
        isSeparateMappingsAndSettings = separateMappingsAndSettings;
    }
}
