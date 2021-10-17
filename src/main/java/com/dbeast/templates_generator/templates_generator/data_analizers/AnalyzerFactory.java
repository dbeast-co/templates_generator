package com.dbeast.templates_generator.templates_generator.data_analizers;

import com.dbeast.templates_generator.constants.EFieldTypes;
import com.dbeast.templates_generator.templates_generator.pojo.DateFormatsPOJO;

import java.util.List;

public class AnalyzerFactory {
    private final IntegerAnalyzer integerAnalyzer;
    private final LongAnalyzer longAnalyzer;
    private final BooleanAnalyzer booleanAnalyzer;
    private final TextAnalyzer textAnalyzer;
    private final DateAnalyzer dateAnalyzer;
    private final FloatAnalyzer floatAnalyzer;
    private final DoubleAnalyzer doubleAnalyzer;
    private final KeywordAnalyzer keywordAnalyzer;
    private final IPAnalyzer ipAnalyzer;
    private final TextAndKeywordAnalyzer textAndKeywordAnalyzer;


    public AnalyzerFactory(final List<DateFormatsPOJO> dateFormats, final List<String> projectExecutionLog) {
        this.textAnalyzer = new TextAnalyzer(dateFormats, projectExecutionLog);
        this.dateAnalyzer = new DateAnalyzer(dateFormats, projectExecutionLog);
        this.booleanAnalyzer = new BooleanAnalyzer(projectExecutionLog);
        this.longAnalyzer = new LongAnalyzer(projectExecutionLog);
        this.integerAnalyzer = new IntegerAnalyzer(projectExecutionLog);
        this.floatAnalyzer = new FloatAnalyzer(projectExecutionLog);
        this.doubleAnalyzer = new DoubleAnalyzer(projectExecutionLog);
        this.keywordAnalyzer = new KeywordAnalyzer(projectExecutionLog);
        this.ipAnalyzer = new IPAnalyzer(projectExecutionLog);
        this.textAndKeywordAnalyzer = new TextAndKeywordAnalyzer(projectExecutionLog);
    }

    public AbstractAnalyzer getAnalyzerByFieldType(final String fieldTypeAsString){
        EFieldTypes fieldTypeFromString = EFieldTypes.getTypeByValue(fieldTypeAsString);
        switch (fieldTypeFromString){
            case IP:
                return ipAnalyzer;
            case DATE:
                return dateAnalyzer;
            case LONG:
                return longAnalyzer;
            case TEXT:
                return textAnalyzer;
            case FLOAT:
                return floatAnalyzer;
            case DOUBLE:
                return doubleAnalyzer;
            case BOOLEAN:
                return booleanAnalyzer;
            case INTEGER:
                return integerAnalyzer;
            case KEYWORD:
                return keywordAnalyzer;
            case TEXT_AND_KEYWORD:
                return textAndKeywordAnalyzer;
            default:
                return keywordAnalyzer;
        }
    }
}
