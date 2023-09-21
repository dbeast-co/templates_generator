package com.dbeast.templates_generator.templates_generator.pojo.ui_pojo.project_output;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ComponentTemplateOutputPOJO {

    @JsonProperty("template")
    TemplateOutputPOJO template;

    public TemplateOutputPOJO getTemplate() {
        return template;
    }

    public void setTemplate(TemplateOutputPOJO template) {
        this.template = template;
    }
}
