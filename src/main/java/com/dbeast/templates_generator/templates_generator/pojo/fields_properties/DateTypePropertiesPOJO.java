package com.dbeast.templates_generator.templates_generator.pojo.fields_properties;

import com.dbeast.templates_generator.constants.EFieldTypes;

public class DateTypePropertiesPOJO extends FieldTypePropertiesPOJO {
    private String format;

    public DateTypePropertiesPOJO(final EFieldTypes type, final String format) {
        super(type);
        this.format = format;
    }

    public DateTypePropertiesPOJO clone() throws CloneNotSupportedException {
        return (DateTypePropertiesPOJO) super.clone();
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}
