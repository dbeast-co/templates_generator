package com.dbeast.templates_generator.templates_generator.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

public class IndexSettingsPOJO implements Cloneable {
    @JsonProperty("number_of_shards")
    private int numberOfShards = 1;
    @JsonProperty("number_of_replicas")
    private int numberOfReplicas = 1;
    @JsonProperty("refresh_interval")
    private String refreshInterval = "30s";
    @JsonProperty("codec")
    private String codec = "best_compression";

    public IndexSettingsPOJO() {
    }

    @Override
    public IndexSettingsPOJO clone() {
        try {
            return (IndexSettingsPOJO) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return this;
        }
    }

    public Map<String, Map<String, Object>> settingsAsMap(Map<String, Map<String, Object>> indexSettings) {
        Map<String, Object> indexInnerSettings = new HashMap<>();
        indexInnerSettings.put("codec", getCodec());
        indexInnerSettings.put("number_of_shards", getNumberOfShards());
        indexInnerSettings.put("number_of_replicas", getNumberOfReplicas());
        indexInnerSettings.put("refresh_interval", getRefreshInterval());
        indexSettings.put("index", indexInnerSettings);
        return indexSettings;
    }

    public int getNumberOfShards() {
        return numberOfShards;
    }

    public void setNumberOfShards(int numberOfShards) {
        this.numberOfShards = numberOfShards;
    }

    public int getNumberOfReplicas() {
        return numberOfReplicas;
    }

    public void setNumberOfReplicas(int numberOfReplicas) {
        this.numberOfReplicas = numberOfReplicas;
    }

    public String getRefreshInterval() {
        return refreshInterval;
    }

    public void setRefreshInterval(String refreshInterval) {
        this.refreshInterval = refreshInterval;
    }

    public String getCodec() {
        return codec;
    }

    public void setCodec(String codec) {
        this.codec = codec;
    }

}
