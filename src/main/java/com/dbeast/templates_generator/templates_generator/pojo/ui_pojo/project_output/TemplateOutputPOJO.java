package com.dbeast.templates_generator.templates_generator.pojo.ui_pojo.project_output;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TemplateOutputPOJO {
    @JsonProperty("order")
    private int order;
    @JsonProperty("index_patterns")
    private List<String> indexPatterns = new LinkedList<>();
    @JsonProperty("settings")
    private Map<String, Map<String, Object>> settings = new HashMap<>();
//    private IndexInnerSettingsPOJO settings;
    @JsonProperty("mappings")
    private Map<String, Object> mappings;
    @JsonProperty("aliases")
    private Map<String, Map<String, String>> aliases= new HashMap<>();

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public List<String> getIndexPatterns() {
        return indexPatterns;
    }

    public void setIndexPatterns(List<String> indexPatterns) {
        this.indexPatterns = indexPatterns;
    }

    public Map<String, Object> getMappings() {
        return mappings;
    }

    public void setMappings(Map<String, Object> mappings) {
        this.mappings = mappings;
    }

    public Map<String, Map<String, Object>> getSettings() {
        return settings;
    }

    public void setSettings(Map<String, Map<String, Object>> settings) {
        this.settings = settings;
    }

    public void setSettings( IndexInnerSettingsPOJO settings) {
        this.settings = settings.settingsAsMap(this.settings);
    }

    public Map<String, Map<String, String>> getAliases() {
        return aliases;
    }

    public void setAliases(Map<String, Map<String, String>> aliases) {
        this.aliases = aliases;
    }
}
