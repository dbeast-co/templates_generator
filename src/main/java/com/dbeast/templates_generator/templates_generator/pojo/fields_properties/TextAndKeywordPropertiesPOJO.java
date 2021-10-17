package com.dbeast.templates_generator.templates_generator.pojo.fields_properties;

import com.dbeast.templates_generator.constants.EFieldTypes;

public class TextAndKeywordPropertiesPOJO extends FieldTypePropertiesPOJO{

    private InnerFieldProperty fields = new InnerFieldProperty();


    public TextAndKeywordPropertiesPOJO(final EFieldTypes type) {
        super(type);
    }

    public TextAndKeywordPropertiesPOJO clone() throws CloneNotSupportedException {
        return (TextAndKeywordPropertiesPOJO) super.clone();
    }

    public static class InnerFieldProperty{
        private IgnoreAbovePropertiesPOJO keyword = new IgnoreAbovePropertiesPOJO(EFieldTypes.KEYWORD);

        public IgnoreAbovePropertiesPOJO getKeyword() {
            return keyword;
        }

        public void setKeyword(IgnoreAbovePropertiesPOJO keyword) {
            this.keyword = keyword;
        }
    }

    public InnerFieldProperty getFields() {
        return fields;
    }

    public void setFields(InnerFieldProperty fields) {
        this.fields = fields;
    }

    public void addNormalizer(){
        fields.getKeyword().setNormalizer();
    }

}
