package com.dbeast.templates_generator.templates_generator.data_analizers;

import com.dbeast.templates_generator.constants.EFieldTypes;
import com.dbeast.templates_generator.constants.EAppMessages;
import com.dbeast.templates_generator.templates_generator.pojo.fields_properties.FieldTypePropertiesPOJO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

public abstract class AbstractAnalyzer implements IAnalyzer {
    protected static final Logger logger = LogManager.getLogger();

    protected final List<String> mappingChangeLog;
    final List<EFieldTypes> brakeTypes;
    final List<EFieldTypes> changeTypes;
    final List<EFieldTypes> defaultTypes;
    final FieldTypePropertiesPOJO fieldTypePropertyOnChange;
    final FieldTypePropertiesPOJO fieldTypePropertyOnDefault;
    final EFieldTypes onChangeFieldType;
    final String onChangeLogMessage;
    final String onDefaultLogMessage;

    final List<EFieldTypes> compatibleTypes;

    public AbstractAnalyzer(final List<EFieldTypes> brakeTypes,
                            final List<EFieldTypes> changeTypes,
                            final List<EFieldTypes> defaultTypes,
                            final FieldTypePropertiesPOJO fieldTypePropertyOnChange,
                            final FieldTypePropertiesPOJO fieldTypePropertyOnDefault,
                            final EFieldTypes onChangeFieldType,
                            final EFieldTypes onDefaultFieldType,
                            final List<String> mappingChangeLog) {
        this.brakeTypes = brakeTypes;
        this.changeTypes = changeTypes;
        this.defaultTypes = defaultTypes;
        this.fieldTypePropertyOnChange = fieldTypePropertyOnChange;
        this.fieldTypePropertyOnDefault = fieldTypePropertyOnDefault;
        this.onChangeFieldType = onChangeFieldType;
        this.onChangeLogMessage = String.format(EAppMessages.ON_CHANGE_LOG_MESSAGE.getMessage(), "%s", "%s", onChangeFieldType);
        this.onDefaultLogMessage = String.format(EAppMessages.ON_CHANGE_LOG_MESSAGE.getMessage(), "%s", "%s", onDefaultFieldType);

        this.mappingChangeLog = mappingChangeLog;
        this.compatibleTypes = null;
    }

    public AbstractAnalyzer(final List<EFieldTypes> brakeTypes,
                            final List<EFieldTypes> changeTypes,
                            final List<EFieldTypes> defaultTypes,
                            final FieldTypePropertiesPOJO fieldTypePropertyOnChange,
                            final FieldTypePropertiesPOJO fieldTypePropertyOnDefault,
                            final EFieldTypes onChangeFieldType,
                            final EFieldTypes onDefaultFieldType,
                            final List<String> mappingChangeLog,
                            List<EFieldTypes> compatibleTypes) {
        this.brakeTypes = brakeTypes;
        this.changeTypes = changeTypes;
        this.defaultTypes = defaultTypes;
        this.fieldTypePropertyOnChange = fieldTypePropertyOnChange;
        this.fieldTypePropertyOnDefault = fieldTypePropertyOnDefault;
        this.onChangeFieldType = onChangeFieldType;
        this.onChangeLogMessage = String.format(EAppMessages.ON_CHANGE_LOG_MESSAGE.getMessage(), "%s", "%s", onChangeFieldType);
        this.onDefaultLogMessage = String.format(EAppMessages.ON_CHANGE_LOG_MESSAGE.getMessage(), "%s", "%s", onDefaultFieldType);

        this.mappingChangeLog = mappingChangeLog;
        this.compatibleTypes = compatibleTypes;
    }

    @Override
    public void analyze(String key, Map<String, Object> schema) {
        if (key.equals("size_in_bites")) {
            System.out.println("stop");
        }
        if (schema.containsKey(key)) {
            compareAndUpdate(key, schema);
        } else {
            schema.put(key, fieldTypePropertyOnChange);
        }
    }

    @Override
    public void compareAndUpdate(final String key, final Map<String, Object> schema) {
        for (EFieldTypes type : brakeTypes) {
            if (type.equals(((FieldTypePropertiesPOJO) schema.get(key)).enumType())) {
                return;
            }
        }
        for (EFieldTypes type : changeTypes) {
            if (type.equals(((FieldTypePropertiesPOJO) schema.get(key)).enumType())) {
                if(logger.isDebugEnabled()) {
                    logger.debug(String.format(onChangeLogMessage, key, type));
                }
                schema.put(key, fieldTypePropertyOnChange);
                mappingChangeLog.add(String.format(onChangeLogMessage, key, type));
                return;
            }
        }
        for (EFieldTypes type : defaultTypes) {
            if (type.equals(((FieldTypePropertiesPOJO) schema.get(key)).enumType())) {
                if(logger.isDebugEnabled()) {
                    logger.debug(String.format(onDefaultLogMessage, key, type));
                }
                schema.put(key, fieldTypePropertyOnDefault);
                mappingChangeLog.add(String.format(onChangeLogMessage, key, type));
                return;
            }
        }
    }

    public boolean isConflictInField(EFieldTypes fieldType){
        return brakeTypes.contains(fieldType);
    }
}
