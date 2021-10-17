package com.dbeast.templates_generator.templates_generator.data_analizers;

import com.dbeast.templates_generator.constants.EFieldTypes;
import com.dbeast.templates_generator.templates_generator.pojo.fields_properties.FieldTypePropertiesPOJO;
import com.dbeast.templates_generator.templates_generator.pojo.fields_properties.IgnoreAbovePropertiesPOJO;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IPAnalyzer extends AbstractAnalyzer implements IAnalyzer {
    public IPAnalyzer(final List<String> mappingChangeLog) {
        super(
                Arrays.asList( EFieldTypes.KEYWORD, EFieldTypes.TEXT, EFieldTypes.IP, EFieldTypes.TEXT_AND_KEYWORD),
                new LinkedList<>(),
                Arrays.asList(EFieldTypes.FLOAT, EFieldTypes.DOUBLE, EFieldTypes.INTEGER, EFieldTypes.LONG, EFieldTypes.BOOLEAN, EFieldTypes.DATE),
                new FieldTypePropertiesPOJO(EFieldTypes.IP),
                new IgnoreAbovePropertiesPOJO(EFieldTypes.KEYWORD),
                EFieldTypes.IP,
                EFieldTypes.KEYWORD,
                mappingChangeLog);
    }

    public boolean isIPAddress(String value) {
        String zeroTo255 = "(\\d{1,2}|([01])\\d{2}|2[0-4]\\d|25[0-5])";
        String Ipregex = zeroTo255 + "\\." + zeroTo255 + "\\." + zeroTo255 + "\\." + zeroTo255;
        Pattern p = Pattern.compile(Ipregex);
        if (value == null) {
            return false;
        }
        Matcher m = p.matcher(value);
        return m.matches();
    }

//    @Override
//    public void compareAndUpdate(String key, Map<String, Object> schema) {
//        if (IndexAnalyzer.isDebugMode && key.equals(IndexAnalyzer.fieldForTest)) {
//            System.out.println("stop! New - IP, old: " + ((FieldTypePropertiesPOJO) schema.get(key)).enumType());
//        }
//        switch (((FieldTypePropertiesPOJO) schema.get(key)).enumType()) {
//            case IP:
//            case KEYWORD:
//            case TEXT:
//                break;
//            case FLOAT:
//            case DOUBLE:
//            case DATE:
//            case BOOLEAN:
//            case INTEGER:
//            case LONG:
//            default:
//                logger.info("Field: " + key + " changed from " + ((FieldTypePropertiesPOJO) schema.get(key)).enumType() + " to " + EFieldTypes.KEYWORD);
//                schema.put(key, new FieldTypePropertiesPOJO(EFieldTypes.KEYWORD));
//
//        }
//    }


}
