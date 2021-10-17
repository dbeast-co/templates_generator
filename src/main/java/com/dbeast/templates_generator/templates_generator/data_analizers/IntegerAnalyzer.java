package com.dbeast.templates_generator.templates_generator.data_analizers;

import com.dbeast.templates_generator.constants.EFieldTypes;
import com.dbeast.templates_generator.templates_generator.pojo.fields_properties.FieldTypePropertiesPOJO;
import com.dbeast.templates_generator.templates_generator.pojo.fields_properties.IgnoreAbovePropertiesPOJO;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IntegerAnalyzer extends AbstractAnalyzer implements IAnalyzer {

    public IntegerAnalyzer(final List<String> mappingChangeLog) {
        super(
                Arrays.asList(EFieldTypes.FLOAT, EFieldTypes.DOUBLE, EFieldTypes.INTEGER, EFieldTypes.LONG, EFieldTypes.KEYWORD, EFieldTypes.TEXT, EFieldTypes.TEXT_AND_KEYWORD),
                new LinkedList<>(),
                Arrays.asList(EFieldTypes.BOOLEAN, EFieldTypes.DATE, EFieldTypes.IP),
                new FieldTypePropertiesPOJO(EFieldTypes.INTEGER),
                new IgnoreAbovePropertiesPOJO(EFieldTypes.KEYWORD),
                EFieldTypes.INTEGER,
                EFieldTypes.KEYWORD,
                mappingChangeLog);
    }

    public boolean isNumber(String value, int numberOfDigits) {
        Pattern LONG_PATTERN = Pattern.compile("^-?\\d{1," + numberOfDigits + "}$");
        if (value == null) {
            return false;
        }
        Matcher m = LONG_PATTERN.matcher(value);
        return m.matches();
    }

//    @Override
//    public void compareAndUpdate(String key, Map<String, Object> schema) {
//        if (IndexAnalyzer.isDebugMode && key.equals(IndexAnalyzer.fieldForTest)) {
//            System.out.println("stop! New - integer, old: " + ((FieldTypePropertiesPOJO) schema.get(key)).enumType());
//        }
//        switch (((FieldTypePropertiesPOJO) schema.get(key)).enumType()) {
//            case INTEGER:
//            case KEYWORD:
//            case LONG:
//            case TEXT:
//            case FLOAT:
//            case DOUBLE:
//                break;
//            case BOOLEAN:
//            case DATE:
//            case IP:
//            default:
//                logger.info("Field: " + key + " changed from " + ((FieldTypePropertiesPOJO) schema.get(key)).enumType() + " to " + EFieldTypes.KEYWORD);
//                schema.put(key, new FieldTypePropertiesPOJO(EFieldTypes.KEYWORD));
//        }
//    }
}
