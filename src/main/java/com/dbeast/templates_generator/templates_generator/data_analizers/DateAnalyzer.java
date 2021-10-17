package com.dbeast.templates_generator.templates_generator.data_analizers;

import com.dbeast.templates_generator.constants.EFieldTypes;
import com.dbeast.templates_generator.templates_generator.pojo.DateFormatsPOJO;
import com.dbeast.templates_generator.templates_generator.pojo.fields_properties.DateTypePropertiesPOJO;
import com.dbeast.templates_generator.templates_generator.pojo.fields_properties.FieldTypePropertiesPOJO;
import com.dbeast.templates_generator.templates_generator.pojo.fields_properties.IgnoreAbovePropertiesPOJO;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class DateAnalyzer extends AbstractAnalyzer implements IAnalyzer {
    private final List<DateFormatsPOJO> dateFormats;

    public DateAnalyzer(List<DateFormatsPOJO> dateFormats, final List<String> mappingChangeLog) {
        super(
                Arrays.asList(EFieldTypes.LONG, EFieldTypes.DOUBLE, EFieldTypes.KEYWORD, EFieldTypes.TEXT, EFieldTypes.DATE, EFieldTypes.TEXT_AND_KEYWORD),
                Arrays.asList(EFieldTypes.INTEGER),
                Arrays.asList(EFieldTypes.FLOAT, EFieldTypes.BOOLEAN, EFieldTypes.IP),
                new FieldTypePropertiesPOJO(EFieldTypes.DATE),
                new IgnoreAbovePropertiesPOJO(EFieldTypes.KEYWORD),
                EFieldTypes.LONG,
                EFieldTypes.KEYWORD,
                mappingChangeLog);
        this.dateFormats = dateFormats;
    }

    public boolean isDateAndUpdate(String key, Map<String, Object> schema, final long value) {
        if ((value > 1000000000000L && value < 2000000000000L) ||
                (value > 1000000000L && value < 2000000000L)
        ) {
            analyze(key, schema);
            return true;
        }
        return false;
    }
    public boolean isDateAndUpdateForDouble(String key, Map<String, Object> schema, final long value) {
        if ((value > 1000000000000L && value < 2000000000000L) ||
                (value > 1000000000L && value < 2000000000L)
        ) {
            analyze(key, schema);
            return true;
        }
        return false;
    }

    public boolean isDateAndUpdate(String key, Map<String, Object> schema, final double value, final String stringValue) {
        if (value > 1000000000.0 && value < 2000000000.0) {
            String[] splattedField = stringValue.split("\\.");
            if (splattedField.length == 2 && splattedField[1].length() <= 3) {
                analyze(key, schema);
                return true;
            }
        }
        return false;
    }

    public boolean isDateAndUpdate(String key, Map<String, Object> schema, final String value) {
        boolean isDate = false;
        for (DateFormatsPOJO dateFormat : dateFormats) {
            if (isDateAndUpdate(value, dateFormat.getDataFormat())) {
                if (schema.containsKey(key)) {
                    compareAndUpdate(key, schema);
                } else {
                    if (dateFormat.getMappingType().equals("strict_date_optional_time")) {
                        schema.put(key, new FieldTypePropertiesPOJO(EFieldTypes.DATE));
                    } else {
                        schema.put(key, new DateTypePropertiesPOJO(EFieldTypes.DATE, dateFormat.getMappingType()));
                    }
                }
                isDate = true;
                break;
            }
        }
        return isDate;
    }

    private boolean isDateAndUpdate(String value, String dateFormat) {
        try {
            if (value.length() <= dateFormat.length()) {
                new SimpleDateFormat(dateFormat).parse(value);
                return true;
            } else {
                return false;
            }
        } catch (ParseException e) {
            return false;
        }
    }

}