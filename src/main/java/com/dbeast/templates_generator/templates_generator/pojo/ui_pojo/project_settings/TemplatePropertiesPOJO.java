package com.dbeast.templates_generator.templates_generator.pojo.ui_pojo.project_settings;

import com.dbeast.templates_generator.templates_generator.pojo.IndexSettingsPOJO;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class TemplatePropertiesPOJO implements Cloneable {
    @JsonProperty("template_name")
    private String templateName;
    @JsonProperty("index_patterns")
    private List<String> indexPatterns = new LinkedList<>();
    @JsonProperty("order")
    private int order;
    @JsonProperty("index_settings")
    private IndexSettingsPOJO indexSettings = new IndexSettingsPOJO();

    @JsonProperty("alias")
    private String alias;

    @JsonProperty("existing_template_actions")
    private ExistingTemplateActions existingTemplateActions = new ExistingTemplateActions();

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("is_add_fields_from_existing_template")
    private boolean isAddFieldsFromExistingTemplate;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("is_add_normalizer_to_keyword_fields")
    private boolean isAddNormalizerToKeywordFields;

    @Override
    public TemplatePropertiesPOJO clone() {
        try {
            TemplatePropertiesPOJO result = (TemplatePropertiesPOJO) super.clone();
            result.indexSettings = indexSettings.clone();
            return result;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return this;
        }
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        if (templateName!=null && !templateName.isEmpty()) {
            this.templateName = templateName.trim();
        } else {
            this.templateName = templateName;
        }
    }

    @JsonProperty("index_patterns")
    public String getIndexPatterns() {
        if (indexPatterns.size() > 0) {
            return indexPatterns.stream().map(index -> "," + index).collect(Collectors.joining()).replaceFirst(",", "");
        } else {
            return "";
        }
    }

    public List<String> generateIndexPatternsForTemplate() {
        return indexPatterns;
    }

    public void setIndexPatterns(String indexPatterns) {
        this.indexPatterns = Arrays.asList(indexPatterns.replaceAll(" ", "").split(","));
    }

    public void transferIndexPatternsFromCLIProject(List<String> indexPatterns) {
        this.indexPatterns = indexPatterns;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
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

    public boolean isAddNormalizerToKeywordFields() {
        return isAddNormalizerToKeywordFields;
    }

    public void setAddNormalizerToKeywordFields(boolean addNormalizerToKeywordFields) {
        isAddNormalizerToKeywordFields = addNormalizerToKeywordFields;
    }
}
