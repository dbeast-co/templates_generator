package com.dbeast.templates_generator.templates_generator.pojo.ui_pojo.project_output;

import com.dbeast.templates_generator.templates_generator.pojo.IndexSettingsPOJO;

import java.util.Map;

public class IndexInnerSettingsPOJO {
    private final IndexSettingsPOJO index;

    public IndexInnerSettingsPOJO(IndexSettingsPOJO indexSettings) {
        this.index = indexSettings;
    }

    public IndexSettingsPOJO getIndex() {
        return index;
    }

    public Map<String, Map<String, Object>> settingsAsMap(Map<String, Map<String, Object>> indexSettings){
        return index.settingsAsMap(indexSettings);
    }
}
