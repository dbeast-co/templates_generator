package com.dbeast.templates_generator.templates_generator.pojo.cli_pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CLIIndexPropertiesPOJO {
    @JsonProperty("index_name")
    private String indexName;

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }
}
