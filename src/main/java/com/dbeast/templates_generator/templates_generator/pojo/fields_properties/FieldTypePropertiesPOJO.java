package com.dbeast.templates_generator.templates_generator.pojo.fields_properties;

import com.dbeast.templates_generator.constants.EFieldTypes;

public class FieldTypePropertiesPOJO {
    private EFieldTypes type;

    public FieldTypePropertiesPOJO(EFieldTypes type) {
        this.type = type;
    }

    public String getType() {
        return type.getStringValueOfProperty();
    }

    public FieldTypePropertiesPOJO clone() throws CloneNotSupportedException {
        return (FieldTypePropertiesPOJO) super.clone();
    }

    public EFieldTypes enumType() {
        return type;
    }

    public void setType(EFieldTypes type) {
        this.type = type;
    }

}
