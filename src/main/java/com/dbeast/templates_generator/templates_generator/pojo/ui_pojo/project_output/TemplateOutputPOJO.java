package com.dbeast.templates_generator.templates_generator.pojo.ui_pojo.project_output;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

public class TemplateOutputPOJO {
    @JsonProperty("settings")
    private Map<String, Map<String, Object>> settings = new HashMap<>();
    @JsonProperty("mappings")
    private Map<String, Object> mappings = new HashMap<>();
    @JsonProperty("aliases")
    private Map<String, Object> aliases= new HashMap<>();


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

    public Map<String, Object> getAliases() {
        return aliases;
    }

    public void setAliases(Map<String, Object> aliases) {
        this.aliases = aliases;
    }
}
