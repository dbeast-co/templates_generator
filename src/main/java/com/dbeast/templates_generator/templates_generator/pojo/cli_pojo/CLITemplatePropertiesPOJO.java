package com.dbeast.templates_generator.templates_generator.pojo.cli_pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class CLITemplatePropertiesPOJO {
    @JsonProperty("template_name")
    private String templateName;
    @JsonProperty("index_patterns")
    private List<String> indexPatterns;
    @JsonProperty("order")
    private int order;


    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public List<String> getIndexPatterns() {
        return indexPatterns;
    }

    public void setIndexPatterns(List<String> indexPatterns) {
        this.indexPatterns = indexPatterns;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
