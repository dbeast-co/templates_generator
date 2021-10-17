package com.dbeast.templates_generator.constants;

import java.util.Arrays;

public enum EFieldTypes {
    KEYWORD("keyword"),
    TEXT("text"),
    BOOLEAN("boolean"),
    IP("ip"),
    INTEGER("integer"),
    LONG("long"),
    DOUBLE("double"),
    FLOAT("float"),
    DATE("date"),
    TEXT_AND_KEYWORD("text and keyword");

    private final String fieldTypeValue;

    EFieldTypes(final String statusDescription){
        this.fieldTypeValue = statusDescription;
    }

    public String getStringValueOfProperty() {
        return fieldTypeValue;
    }

    public static EFieldTypes getTypeByValue(String value){
        return Arrays.stream(EFieldTypes.values()).filter(enumValue -> enumValue.fieldTypeValue.equals(value)).findFirst().orElse(KEYWORD);
    }
}
