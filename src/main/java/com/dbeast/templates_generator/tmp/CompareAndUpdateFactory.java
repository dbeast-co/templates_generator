package com.dbeast.templates_generator.tmp;

import com.dbeast.templates_generator.constants.EAppMessages;
import com.dbeast.templates_generator.constants.EFieldTypes;
import com.dbeast.templates_generator.templates_generator.pojo.fields_properties.FieldTypePropertiesPOJO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class CompareAndUpdateFactory {
    protected static final Logger logger = LogManager.getLogger();
    final List<FieldTypePropertiesPOJO> brakeTypes;
    final List<FieldTypePropertiesPOJO> changeTypes;
    final List<FieldTypePropertiesPOJO> defaultTypes;
    final FieldTypePropertiesPOJO changeDataType;
    final FieldTypePropertiesPOJO defaultDataType;
    final EFieldTypes changeType;
    final EFieldTypes defaultType;
    final String onChangeLogMessage;
    final String onDefaultLogMessage;

    public CompareAndUpdateFactory(final List<FieldTypePropertiesPOJO> brakeTypes,
                                   final List<FieldTypePropertiesPOJO> changeTypes,
                                   final List<FieldTypePropertiesPOJO> defaultTypes,
                                   final FieldTypePropertiesPOJO changeDataType,
                                   final FieldTypePropertiesPOJO defaultDataType,
                                   final EFieldTypes changeType,
                                   final EFieldTypes defaultType) {
        this.brakeTypes = brakeTypes;
        this.changeTypes = changeTypes;
        this.defaultTypes = defaultTypes;
        this.changeDataType = changeDataType;
        this.defaultDataType = defaultDataType;
        this.changeType = changeType;
        this.defaultType = defaultType;
        this.onChangeLogMessage = String.format(EAppMessages.ON_CHANGE_LOG_MESSAGE.getMessage(), "%s", "%s", changeType);
        this.onDefaultLogMessage = String.format(EAppMessages.ON_CHANGE_LOG_MESSAGE.getMessage(), "%s", "%s", defaultType);
    }

    void compareAndUpdate(final String key, final Map<String, Object> schema) {
        for (FieldTypePropertiesPOJO type : brakeTypes) {
            if (type.enumType().equals(((FieldTypePropertiesPOJO) schema.get(key)).enumType())) {
                return;
            }
        }
        for (FieldTypePropertiesPOJO type : changeTypes) {
            if (type.enumType().equals(((FieldTypePropertiesPOJO) schema.get(key)).enumType())) {
                logger.info(String.format(onChangeLogMessage, key, type.enumType()));
                schema.put(key, changeDataType);
                return;
            }
        }
        for (FieldTypePropertiesPOJO type : defaultTypes) {
            if (type.enumType().equals(((FieldTypePropertiesPOJO) schema.get(key)).enumType())) {
                logger.info(String.format(onDefaultLogMessage, key, type.enumType()));
                schema.put(key, defaultDataType);
                return;
            }
        }
    }

//    public void compareAndUpdate(String key, Map<String, Object> schema) {
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
