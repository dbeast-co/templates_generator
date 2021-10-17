package com.dbeast.templates_generator.templates_generator.data_analizers;

import com.dbeast.templates_generator.constants.EFieldTypes;
import com.dbeast.templates_generator.templates_generator.pojo.fields_properties.TextAndKeywordPropertiesPOJO;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class TextAndKeywordAnalyzer extends AbstractAnalyzer implements IAnalyzer {
    public TextAndKeywordAnalyzer(final List<String> mappingChangeLog) {
        super(
                Arrays.asList(EFieldTypes.TEXT),
                new LinkedList<>(),
                Arrays.asList( EFieldTypes.KEYWORD, EFieldTypes.IP, EFieldTypes.FLOAT, EFieldTypes.DOUBLE, EFieldTypes.INTEGER, EFieldTypes.LONG, EFieldTypes.BOOLEAN, EFieldTypes.DATE),
                new TextAndKeywordPropertiesPOJO(EFieldTypes.TEXT),
                new TextAndKeywordPropertiesPOJO(EFieldTypes.TEXT),
                EFieldTypes.TEXT_AND_KEYWORD,
                EFieldTypes.TEXT_AND_KEYWORD,
                mappingChangeLog);
    }

}


