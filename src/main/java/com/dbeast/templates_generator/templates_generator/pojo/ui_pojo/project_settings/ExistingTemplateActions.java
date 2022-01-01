package com.dbeast.templates_generator.templates_generator.pojo.ui_pojo.project_settings;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ExistingTemplateActions {
    @JsonProperty("is_add_all_fields")
    private boolean isAddAllFields= true;

    @JsonProperty("is_add_fields_from_at_least_one_time_used_root_level")
    private boolean isAddFieldsFromAtLeastOneTimeUsedRootLevel;

    @JsonProperty("is_replace_existing_field_types")
    private boolean isReplaceExistingFieldTypes = true;


    @JsonProperty("is_generate_dedicated_none_existing_fields_template")
    private boolean isGenerateDedicatedNoneExistingFieldsTemplate;


    @JsonProperty("is_add_settings")
    private boolean isAddSettings;


    @JsonProperty("is_add_dynamic_mapping")
    private boolean isAddDynamicMapping;


    @JsonProperty("is_ignore_type_conflicts")
    private boolean isIgnoreTypeConflicts = true;


    @JsonProperty("add_fields_from_existing_template_name")
    private String addFieldsFromExistingTemplateName;

    @JsonProperty("is_add_all_fields")
    public boolean isAddAllFields() {
        return isAddAllFields;
    }

    public void setAddAllFields(boolean addAllFields) {
        isAddAllFields = addAllFields;
    }

    @JsonProperty("is_add_fields_from_at_least_one_time_used_root_level")
    public boolean isAddFieldsFromAtLeastOneTimeUsedRootLevel() {
        return isAddFieldsFromAtLeastOneTimeUsedRootLevel;
    }

    public void setAddFieldsFromAtLeastOneTimeUsedRootLevel(boolean addFieldsFromAtLeastOneTimeUsedRootLevel) {
        isAddFieldsFromAtLeastOneTimeUsedRootLevel = addFieldsFromAtLeastOneTimeUsedRootLevel;
    }

    @JsonProperty("is_replace_existing_field_types")
    public boolean isReplaceExistingFieldTypes() {
        return isReplaceExistingFieldTypes;
    }

    public void setReplaceExistingFieldTypes(boolean replaceExistingFieldTypes) {
        isReplaceExistingFieldTypes = replaceExistingFieldTypes;
    }

    @JsonProperty("is_generate_dedicated_none_existing_fields_template")
    public boolean isGenerateDedicatedNoneExistingFieldsTemplate() {
        return isGenerateDedicatedNoneExistingFieldsTemplate;
    }

    public void setGenerateDedicatedNoneExistingFieldsTemplate(boolean generateDedicatedNoneExistingFieldsTemplate) {
        isGenerateDedicatedNoneExistingFieldsTemplate = generateDedicatedNoneExistingFieldsTemplate;
    }

    @JsonProperty("is_add_settings")
    public boolean isAddSettings() {
        return isAddSettings;
    }

    public void setAddSettings(boolean addSettings) {
        isAddSettings = addSettings;
    }

    @JsonProperty("is_add_dynamic_mapping")
    public boolean isAddDynamicMapping() {
        return isAddDynamicMapping;
    }

    public void setAddDynamicMapping(boolean addDynamicMapping) {
        isAddDynamicMapping = addDynamicMapping;
    }

    @JsonProperty("is_ignore_type_conflicts")
    public boolean isIgnoreTypeConflicts() {
        return isIgnoreTypeConflicts;
    }

    public void setIgnoreTypeConflicts(boolean ignoreTypeConflicts) {
        isIgnoreTypeConflicts = ignoreTypeConflicts;
    }

    public String getAddFieldsFromExistingTemplateName() {
        return addFieldsFromExistingTemplateName;
    }

    public void setAddFieldsFromExistingTemplateName(String addFieldsFromExistingTemplateName) {
        if (addFieldsFromExistingTemplateName!= null && !addFieldsFromExistingTemplateName.isEmpty()) {
            this.addFieldsFromExistingTemplateName = addFieldsFromExistingTemplateName.trim();
        } else {
            this.addFieldsFromExistingTemplateName = addFieldsFromExistingTemplateName;
        }
    }
}
