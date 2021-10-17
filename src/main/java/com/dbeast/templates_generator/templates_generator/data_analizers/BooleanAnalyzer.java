package com.dbeast.templates_generator.templates_generator.data_analizers;

import com.dbeast.templates_generator.constants.EFieldTypes;
import com.dbeast.templates_generator.templates_generator.pojo.fields_properties.FieldTypePropertiesPOJO;
import com.dbeast.templates_generator.templates_generator.pojo.fields_properties.IgnoreAbovePropertiesPOJO;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class BooleanAnalyzer extends AbstractAnalyzer implements IAnalyzer {

    public BooleanAnalyzer(final List<String> mappingChangeLog) {
        super(
                Arrays.asList(EFieldTypes.BOOLEAN, EFieldTypes.KEYWORD, EFieldTypes.TEXT, EFieldTypes.TEXT_AND_KEYWORD),
                new LinkedList<>(),
                Arrays.asList(EFieldTypes.FLOAT, EFieldTypes.DOUBLE, EFieldTypes.INTEGER, EFieldTypes.LONG, EFieldTypes.DATE, EFieldTypes.IP),
                new FieldTypePropertiesPOJO(EFieldTypes.BOOLEAN),
                new IgnoreAbovePropertiesPOJO(EFieldTypes.KEYWORD),
                EFieldTypes.BOOLEAN,
                EFieldTypes.KEYWORD,
                mappingChangeLog);
    }

    public boolean isTextBoolean(String value) {
        return (value.equals("true") || value.equals("false"));
    }
}
