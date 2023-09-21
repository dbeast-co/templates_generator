package com.dbeast.templates_generator.templates_generator.pojo.ui_pojo.project_output;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class IndexTemplateOutputPOJO {
    @JsonProperty("index_patterns")
    private List<String> indexPatterns = new LinkedList<>();
    @JsonProperty("composed_of")
    private List<String> composed_of = new LinkedList<>();

    @JsonProperty("template")
    private TemplateOutputPOJO template;

    @JsonProperty("priority")
    private int priority;


    public List<String> getIndexPatterns() {
        return indexPatterns;
    }

    public void setIndexPatterns(List<String> indexPatterns) {
        this.indexPatterns = indexPatterns;
    }

    public List<String> getComposed_of() {
        return composed_of;
    }

    public void setComposed_of(List<String> composed_of) {
        this.composed_of = composed_of;
    }

    public TemplateOutputPOJO getTemplate() {
        return template;
    }

    public void setTemplate(TemplateOutputPOJO template) {
        this.template = template;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
