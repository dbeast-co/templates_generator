package com.dbeast.templates_generator.templates_generator.data_analizers;

import com.dbeast.templates_generator.constants.EFieldTypes;
import com.dbeast.templates_generator.templates_generator.pojo.fields_properties.FieldTypePropertiesPOJO;
import com.dbeast.templates_generator.templates_generator.pojo.fields_properties.IgnoreAbovePropertiesPOJO;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LongAnalyzer extends AbstractAnalyzer implements IAnalyzer {

    public LongAnalyzer(final List<String> mappingChangeLog) {
        super(
                Arrays.asList(EFieldTypes.LONG, EFieldTypes.DOUBLE, EFieldTypes.KEYWORD, EFieldTypes.TEXT, EFieldTypes.TEXT_AND_KEYWORD),
                Arrays.asList(EFieldTypes.INTEGER, EFieldTypes.DATE),
                Arrays.asList(EFieldTypes.FLOAT, EFieldTypes.BOOLEAN, EFieldTypes.IP),
                new FieldTypePropertiesPOJO(EFieldTypes.LONG),
                new IgnoreAbovePropertiesPOJO(EFieldTypes.KEYWORD),
                EFieldTypes.LONG,
                EFieldTypes.KEYWORD,
                mappingChangeLog);
    }

    public boolean isNumber(String value, int minimalDigits, int maximumDigits) {
        Pattern LONG_PATTERN = Pattern.compile("^-?\\d{" + minimalDigits + "," + maximumDigits + "}$");
        if (value == null) {
            return false;
        }
        Matcher m = LONG_PATTERN.matcher(value);
        return m.matches();
    }

//    @Override
//    public void compareAndUpdate(String key, Map<String, Object> schema) {
//        if (IndexAnalyzer.isDebugMode && key.equals(IndexAnalyzer.fieldForTest)) {
//            System.out.println("stop! New - LONG, old: " + ((FieldTypePropertiesPOJO) schema.get(key)).enumType());
//        }
//        switch (((FieldTypePropertiesPOJO) schema.get(key)).enumType()) {
//            case DATE:
//            case INTEGER:
//                logger.info("Field: " + key + " changed from " + ((FieldTypePropertiesPOJO) schema.get(key)).enumType() + " to " + EFieldTypes.LONG);
//                schema.put(key, new FieldTypePropertiesPOJO(EFieldTypes.LONG));
//                break;
//            case LONG:
//            case KEYWORD:
//            case TEXT:
//            case DOUBLE:
//                break;
//            case FLOAT:
//            case IP:
//            case BOOLEAN:
//            default:
//                logger.info("Field: " + key + " changed from " + ((FieldTypePropertiesPOJO) schema.get(key)).enumType() + " to " + EFieldTypes.KEYWORD);
//                schema.put(key, new FieldTypePropertiesPOJO(EFieldTypes.KEYWORD));
//        }
//    }
}
