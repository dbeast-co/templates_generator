package com.dbeast.templates_generator.templates_generator.pojo.ui_pojo.project_settings;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OutputPOJO implements Cloneable{
    @JsonProperty("template_properties")
    private TemplatePropertiesPOJO templateProperties = new TemplatePropertiesPOJO();
    @JsonProperty("index_properties")
    private IndexPropertiesPOJO indexProperties = new IndexPropertiesPOJO();
    @Override
    public OutputPOJO clone() {
        try {
            OutputPOJO result = (OutputPOJO) super.clone();
            result.templateProperties = templateProperties.clone();
            result.indexProperties = indexProperties.clone();
            return result;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return this;
        }
    }
    public TemplatePropertiesPOJO getTemplateProperties() {
        return templateProperties;
    }

    public void setTemplateProperties(TemplatePropertiesPOJO templateProperties) {
        this.templateProperties = templateProperties;
    }

    public IndexPropertiesPOJO getIndexProperties() {
        return indexProperties;
    }

    public void setIndexProperties(IndexPropertiesPOJO indexProperties) {
        this.indexProperties = indexProperties;
    }

}
