package com.dbeast.templates_generator.templates_generator.pojo.ui_pojo.project_settings;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ActionsPOJO implements Cloneable{
    @JsonProperty("is_generate_template")
    private boolean isGenerateTemplate = true;
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
}
