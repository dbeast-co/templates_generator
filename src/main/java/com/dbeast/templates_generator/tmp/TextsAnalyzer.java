package com.dbeast.templates_generator.tmp;

import com.dbeast.templates_generator.TemplatesGenerator;
import com.dbeast.templates_generator.constants.EFieldTypes;
import com.dbeast.templates_generator.templates_generator.data_analizers.*;
import com.dbeast.templates_generator.templates_generator.pojo.DateFormatsPOJO;
import com.dbeast.templates_generator.templates_generator.pojo.fields_properties.FieldTypePropertiesPOJO;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class TextsAnalyzer extends AbstractAnalyzer implements IAnalyzer {

    private final IntegerAnalyzer integerAnalyzer;
    private final LongAnalyzer longAnalyzer;
    private final DoubleAnalyzer doubleAnalyzer;
    private final BooleanAnalyzer booleanAnalyzer;
    private final IPAnalyzer ipAnalyzer;
    private final KeywordAnalyzer keywordAnalyzer ;
    private final TextAndKeywordAnalyzer textAndKeywordAnalyzer;
    private final DateAnalyzer dateAnalyzer;

    public TextsAnalyzer(List<DateFormatsPOJO> dateFormats, final List<String> mappingChangeLog) {
        super(
                Arrays.asList(EFieldTypes.TEXT),
                new LinkedList<>(),
                Arrays.asList(EFieldTypes.BOOLEAN, EFieldTypes.KEYWORD, EFieldTypes.FLOAT, EFieldTypes.DOUBLE, EFieldTypes.INTEGER, EFieldTypes.LONG, EFieldTypes.DATE, EFieldTypes.IP),
                new FieldTypePropertiesPOJO(EFieldTypes.TEXT),
                new FieldTypePropertiesPOJO(EFieldTypes.TEXT),
                EFieldTypes.TEXT,
                EFieldTypes.TEXT,
                mappingChangeLog);
        this.dateAnalyzer = new DateAnalyzer(dateFormats, mappingChangeLog);
        this.booleanAnalyzer = new BooleanAnalyzer(mappingChangeLog);
        this.integerAnalyzer = new IntegerAnalyzer(mappingChangeLog);
        this.longAnalyzer = new LongAnalyzer(mappingChangeLog);
        this.ipAnalyzer = new IPAnalyzer(mappingChangeLog);
        this.keywordAnalyzer = new KeywordAnalyzer(mappingChangeLog);
        this.textAndKeywordAnalyzer = new TextAndKeywordAnalyzer(mappingChangeLog);
        this.doubleAnalyzer = new DoubleAnalyzer(mappingChangeLog);
    }

    //TODO add long dimensions
    public void analyze(Map<String, Object> schema, String value, String key) {
        if (TemplatesGenerator.isDebugMode && key.equals(TemplatesGenerator.fieldForTest) && value.equals("4294967295")) {
            System.out.println("stop! New - TEXT, old: " + ((FieldTypePropertiesPOJO) schema.get(key)).enumType());
        }
        if (value == null || value.isEmpty()) {
            return;
        }
        if (value.length() > 255) {
            analyze(key, schema);
        } else if (longAnalyzer.isNumber(value, 8, 18)) {
            if (!dateAnalyzer.isDateAndUpdate(key, schema, Long.parseLong(value))) {
                longAnalyzer.analyze(key, schema);
            }
        } else if (integerAnalyzer.isNumber(value, 8)) {
            integerAnalyzer.analyze(key, schema);
        } else if (doubleAnalyzer.isDouble(value)) {
            if (!dateAnalyzer.isDateAndUpdate(key, schema, Double.valueOf(value), value)) {
                doubleAnalyzer.analyze(key, schema);
            }
        } else if (ipAnalyzer.isIPAddress(value)) {
            ipAnalyzer.analyze(key, schema);
        } else if (booleanAnalyzer.isTextBoolean(value)) {
            booleanAnalyzer.analyze(key, schema);
        } else if (dateAnalyzer.isDateAndUpdate(key, schema, value)) {
        } else {
            if (containsValues("\\s+", value)) {
                if (value.length() > 20) {
                    textAndKeywordAnalyzer.analyze(key, schema);
                } else {
                    keywordAnalyzer.analyze(key, schema);
                }
            } else {
                keywordAnalyzer.analyze(key, schema);
            }
        }
    }

    static boolean containsValues(String pattern, String text) {
        return Pattern.compile(pattern).matcher(text).find();
    }

}


