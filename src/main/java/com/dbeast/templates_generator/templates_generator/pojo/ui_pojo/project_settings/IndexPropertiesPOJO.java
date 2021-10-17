package com.dbeast.templates_generator.templates_generator.pojo.ui_pojo.project_settings;

import com.dbeast.templates_generator.templates_generator.pojo.IndexSettingsPOJO;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class IndexPropertiesPOJO implements Cloneable {
    @JsonProperty("index_name")
    private String indexName;
    @JsonProperty("index_settings")
    private IndexSettingsPOJO indexSettings = new IndexSettingsPOJO();
    //    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("alias")
    private String alias;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("existing_template_actions")
    private ExistingTemplateActions existingTemplateActions = new ExistingTemplateActions();

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("is_add_fields_from_existing_template")
    private boolean isAddFieldsFromExistingTemplate;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("is_add_normalizer_to_keyword_fields")
    private boolean isAddNormalizerToKeywordFields;

    @Override
    public IndexPropertiesPOJO clone() {
        try {
            IndexPropertiesPOJO result = (IndexPropertiesPOJO) super.clone();
            result.indexSettings = indexSettings.clone();
            return result;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return this;
        }
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public IndexSettingsPOJO getIndexSettings() {
        return indexSettings;
    }

    public void setIndexSettings(IndexSettingsPOJO indexSettings) {
        this.indexSettings = indexSettings;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    @JsonProperty("is_add_fields_from_existing_template")
    public boolean isAddFieldsFromExistingTemplate() {
        return isAddFieldsFromExistingTemplate;
    }

    public void setAddFieldsFromExistingTemplate(boolean addFieldsFromExistingTemplate) {
        isAddFieldsFromExistingTemplate = addFieldsFromExistingTemplate;
    }

    public ExistingTemplateActions getExistingTemplateActions() {
        return existingTemplateActions;
    }

    public void setExistingTemplateActions(ExistingTemplateActions existingTemplateActions) {
        this.existingTemplateActions = existingTemplateActions;
    }

    @JsonProperty("is_add_normalizer_to_keyword_fields")
    public boolean isAddNormalizerToKeywordFields() {
        return isAddNormalizerToKeywordFields;
    }

    public void setAddNormalizerToKeywordFields(boolean addNormalizerToKeywordFields) {
        isAddNormalizerToKeywordFields = addNormalizerToKeywordFields;
    }
}
