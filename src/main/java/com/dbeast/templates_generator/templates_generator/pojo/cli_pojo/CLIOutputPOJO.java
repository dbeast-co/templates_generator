package com.dbeast.templates_generator.templates_generator.pojo.cli_pojo;

import com.dbeast.templates_generator.templates_generator.pojo.IndexSettingsPOJO;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CLIOutputPOJO {
    @JsonProperty("generate_template")
    private boolean isGenerateTemplate = false;
    @JsonProperty("generate_index")
    private boolean isGenerateIndex = false;
    @JsonProperty("template_properties")
    private CLITemplatePropertiesPOJO appTemplateProperties = new CLITemplatePropertiesPOJO();
    @JsonProperty("index_properties")
    private CLIIndexPropertiesPOJO appIndexProperties = new CLIIndexPropertiesPOJO();
    @JsonProperty("index_settings")
    private IndexSettingsPOJO appIndexSettings = new IndexSettingsPOJO();

    @JsonProperty("generate_template")
    public boolean isGenerateTemplate() {
        return isGenerateTemplate;
    }

    public void setGenerateTemplate(boolean generateTemplate) {
        isGenerateTemplate = generateTemplate;
    }

    @JsonProperty("generate_index")
    public boolean isGenerateIndex() {
        return isGenerateIndex;
    }

    public void setGenerateIndex(boolean generateIndex) {
        isGenerateIndex = generateIndex;
    }

    public CLITemplatePropertiesPOJO getAppTemplateProperties() {
        return appTemplateProperties;
    }

    public void setAppTemplateProperties(CLITemplatePropertiesPOJO appTemplateProperties) {
        this.appTemplateProperties = appTemplateProperties;
    }

    public CLIIndexPropertiesPOJO getAppIndexProperties() {
        return appIndexProperties;
    }

    public void setAppIndexProperties(CLIIndexPropertiesPOJO appIndexProperties) {
        this.appIndexProperties = appIndexProperties;
    }

    public IndexSettingsPOJO getAppIndexSettings() {
        return appIndexSettings;
    }

    public void setAppIndexSettings(IndexSettingsPOJO appIndexSettings) {
        this.appIndexSettings = appIndexSettings;
    }
}
