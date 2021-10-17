package com.dbeast.templates_generator.templates_generator.data_analizers;

import com.dbeast.templates_generator.constants.EFieldTypes;
import com.dbeast.templates_generator.templates_generator.pojo.fields_properties.FieldTypePropertiesPOJO;
import com.dbeast.templates_generator.templates_generator.pojo.fields_properties.IgnoreAbovePropertiesPOJO;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class FloatAnalyzer extends AbstractAnalyzer implements IAnalyzer {

    public FloatAnalyzer(final List<String> mappingChangeLog) {
        super(
                Arrays.asList(EFieldTypes.FLOAT, EFieldTypes.DOUBLE, EFieldTypes.KEYWORD, EFieldTypes.TEXT, EFieldTypes.TEXT_AND_KEYWORD),
                new LinkedList<>(),
                Arrays.asList(EFieldTypes.INTEGER, EFieldTypes.BOOLEAN, EFieldTypes.LONG, EFieldTypes.DATE, EFieldTypes.IP),
                new FieldTypePropertiesPOJO(EFieldTypes.FLOAT),
                new IgnoreAbovePropertiesPOJO(EFieldTypes.KEYWORD),
                EFieldTypes.FLOAT,
                EFieldTypes.KEYWORD,
                mappingChangeLog);
    }

    public boolean isFloat(String value) {
        try {
            Float.valueOf(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

//    @Override
//    public void compareAndUpdate(String key, Map<String, Object> schema) {
//        switch (((FieldTypePropertiesPOJO) schema.get(key)).enumType()) {
//            case FLOAT:
//            case DOUBLE:
//            case TEXT:
//            case KEYWORD:
//                break;
//            case INTEGER:
//            case LONG:
//            case BOOLEAN:
//            case DATE:
//            case IP:
//            default:
//                logger.info("Field: " + key + " changed from " + ((FieldTypePropertiesPOJO) schema.get(key)).enumType() + " to " + EFieldTypes.KEYWORD);
//                schema.put(key, new FieldTypePropertiesPOJO(EFieldTypes.KEYWORD));
//        }
//    }
}
