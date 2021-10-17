package com.dbeast.templates_generator.templates_generator.pojo.fields_properties;

import com.dbeast.templates_generator.constants.EAppSettings;
import com.dbeast.templates_generator.constants.EFieldTypes;
import com.fasterxml.jackson.annotation.JsonInclude;

public class IgnoreAbovePropertiesPOJO extends FieldTypePropertiesPOJO {
    private int ignore_above = 256;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String normalizer;

    public IgnoreAbovePropertiesPOJO clone() throws CloneNotSupportedException {
        return (IgnoreAbovePropertiesPOJO) super.clone();
    }

    public IgnoreAbovePropertiesPOJO(EFieldTypes type) {
        super(type);
    }

    public int getIgnore_above() {
        return ignore_above;
    }

    public void setIgnore_above(int ignore_above) {
        this.ignore_above = ignore_above;
    }

    public String getNormalizer() {
        return normalizer;
    }

    public void setNormalizer(String normalizer) {
        this.normalizer = normalizer;
    }

    public void setNormalizer() {
        this.normalizer = EAppSettings.DEFAULT_NORMALIZER_NAME.getConfigurationParameter();
    }
}
