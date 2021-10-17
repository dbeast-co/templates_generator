package com.dbeast.templates_generator.templates_generator.data_analizers;

import com.dbeast.templates_generator.constants.EFieldTypes;
import com.dbeast.templates_generator.templates_generator.pojo.fields_properties.IgnoreAbovePropertiesPOJO;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class KeywordAnalyzer extends AbstractAnalyzer implements IAnalyzer {
    public KeywordAnalyzer(final List<String> mappingChangeLog) {
        super(
                Arrays.asList(EFieldTypes.KEYWORD, EFieldTypes.TEXT, EFieldTypes.TEXT_AND_KEYWORD),
                new LinkedList<>(),
                Arrays.asList(EFieldTypes.FLOAT, EFieldTypes.DOUBLE, EFieldTypes.INTEGER, EFieldTypes.LONG, EFieldTypes.BOOLEAN, EFieldTypes.DATE, EFieldTypes.IP),
                new IgnoreAbovePropertiesPOJO(EFieldTypes.KEYWORD),
                new IgnoreAbovePropertiesPOJO(EFieldTypes.KEYWORD),
                EFieldTypes.KEYWORD,
                EFieldTypes.KEYWORD,
                mappingChangeLog);
    }
//
//    @Override
//    public void compareAndUpdate(String key, Map<String, Object> schema) {
//        if (IndexAnalyzer.isDebugMode && key.equals(IndexAnalyzer.fieldForTest)) {
//            System.out.println("stop! New - KEYWORD, old: " + ((FieldTypePropertiesPOJO) schema.get(key)).enumType());
//        }
//        switch (((FieldTypePropertiesPOJO) schema.get(key)).enumType()) {
//            case KEYWORD:
//            case TEXT:
//                break;
//            case FLOAT:
//            case DOUBLE:
//            case IP:
//            case DATE:
//            case BOOLEAN:
//            case INTEGER:
//            case LONG:
//            default:
//                logger.info("Field: " + key + " changed from " + ((FieldTypePropertiesPOJO) schema.get(key)).enumType() + " to " + EFieldTypes.KEYWORD);
//                schema.put(key, new IgnoreAbovePropertiesPOJO(EFieldTypes.KEYWORD));
//        }
//    }
}
