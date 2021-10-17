package com.dbeast.templates_generator.templates_generator.data_analizers;

import com.dbeast.templates_generator.constants.EFieldTypes;
import com.dbeast.templates_generator.templates_generator.pojo.fields_properties.FieldTypePropertiesPOJO;
import com.dbeast.templates_generator.templates_generator.pojo.fields_properties.IgnoreAbovePropertiesPOJO;

import java.util.Arrays;
import java.util.List;

public class DoubleAnalyzer extends AbstractAnalyzer implements IAnalyzer {
    public DoubleAnalyzer(final List<String> mappingChangeLog) {
        super(
                Arrays.asList(EFieldTypes.DOUBLE, EFieldTypes.KEYWORD, EFieldTypes.TEXT),
                Arrays.asList(EFieldTypes.FLOAT, EFieldTypes.INTEGER,EFieldTypes.LONG, EFieldTypes.DATE),
                Arrays.asList( EFieldTypes.BOOLEAN,  EFieldTypes.IP),
                new FieldTypePropertiesPOJO(EFieldTypes.DOUBLE),
                new IgnoreAbovePropertiesPOJO(EFieldTypes.KEYWORD),
                EFieldTypes.DOUBLE,
                EFieldTypes.KEYWORD,
                mappingChangeLog);
    }

    public boolean isDouble(String value) {
        try {
            Double.valueOf(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
