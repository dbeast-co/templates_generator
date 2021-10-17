package com.dbeast.templates_generator.templates_generator.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DateFormatsPOJO {
    @JsonProperty("data_format")
    private String dataFormat;
    @JsonProperty("mapping_type")
    private String mappingType;

    public String getDataFormat() {
        return dataFormat;
    }

    public void setDataFormat(String dataFormat) {
        this.dataFormat = dataFormat;
    }

    public String getMappingType() {
        return mappingType;
    }

    public void setMappingType(String mappingType) {
        this.mappingType = mappingType;
    }
}
