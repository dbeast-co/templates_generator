package com.dbeast.templates_generator.templates_generator.pojo.ui_pojo.project_settings;

import com.dbeast.templates_generator.templates_generator.pojo.cli_pojo.CLIProjectPOJO;
import com.dbeast.templates_generator.templates_generator.pojo.ui_pojo.ProjectStatusForUIProjectSettingsPagePOJO;
import com.dbeast.templates_generator.utils.GeneralUtils;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.LinkedList;
import java.util.List;

public class ProjectPOJO implements Cloneable {
    @JsonProperty("project_name")
    private String projectName;
    @JsonProperty("project_id")
    private String projectId = GeneralUtils.generateNewUID();
    @JsonProperty("input")
    private InputPOJO inputSettings = new InputPOJO();
    @JsonProperty("output")
    private OutputPOJO outputSettings = new OutputPOJO();
    @JsonProperty("status")
    private ProjectStatusForUIProjectSettingsPagePOJO status = new ProjectStatusForUIProjectSettingsPagePOJO();
    @JsonProperty("actions")
    private ActionsPOJO actions = new ActionsPOJO();
    @JsonProperty("mapping_changes_log")
    private List<String> mappingChangesLog = new LinkedList<>();

    @Override
    public ProjectPOJO clone() {
        try {
            ProjectPOJO result = (ProjectPOJO) super.clone();
            result.inputSettings = inputSettings.clone();
            result.outputSettings = outputSettings.clone();
            result.status = status.clone();
            result.actions = actions.clone();
            return result;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return this;
        }
    }

    public void updateProject(ProjectPOJO project) {
        this.projectName = project.projectName;
        this.inputSettings = project.getInputSettings();
        this.outputSettings = project.getOutputSettings();
        this.actions = project.getActions();
        this.status = new ProjectStatusForUIProjectSettingsPagePOJO();
        this.mappingChangesLog = new LinkedList<>();
    }

    public ProjectPOJO(CLIProjectPOJO project) {
        this.projectName = project.getProjectName();
        this.projectId = project.getProjectId();
        this.inputSettings.getInputSettings().setIndexForAnalyze(project.getInputSettings().getIndexForAnalyze());
        this.inputSettings.getInputSettings().setMaxDocsForAnalyze(project.getInputSettings().getMaxDocsForAnalyze());
        this.inputSettings.getInputSettings().setScrollSize(project.getInputSettings().getScrollSize());
        this.inputSettings.setSourceCluster(project.getInputSettings().getEsSettings());
        this.outputSettings.getIndexProperties().setIndexName(project.getProjectName());
        this.outputSettings.getIndexProperties().setIndexSettings(project.getOutputSettings().getAppIndexSettings());
        this.outputSettings.getTemplateProperties().setTemplateName(project.getOutputSettings().getAppTemplateProperties().getTemplateName());
        this.outputSettings.getTemplateProperties().transferIndexPatternsFromCLIProject(project.getOutputSettings().getAppTemplateProperties().getIndexPatterns());
        this.outputSettings.getTemplateProperties().setOrder(project.getOutputSettings().getAppTemplateProperties().getOrder());
        this.outputSettings.getTemplateProperties().setIndexSettings(project.getOutputSettings().getAppIndexSettings());
        this.actions.setGenerateIndex(project.getOutputSettings().isGenerateIndex());
        this.actions.setGenerateTemplate(project.getOutputSettings().isGenerateTemplate());
    }

    public ProjectPOJO() {
    }


    public List<String> getMappingChangesLog() {
        return mappingChangesLog;
    }

    public void setMappingChangesLog(List<String> mappingChangesLog) {
        if (mappingChangesLog != null) {
            this.mappingChangesLog = mappingChangesLog;
        } else {
            this.mappingChangesLog = new LinkedList<>();
        }
    }

    public void addLogToChangesLog(final String log) {
        this.mappingChangesLog.add(log);
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public InputPOJO getInputSettings() {
        return inputSettings;
    }

    public void setInputSettings(InputPOJO inputSettings) {
        this.inputSettings = inputSettings;
    }

    public OutputPOJO getOutputSettings() {
        return outputSettings;
    }

    public void setOutputSettings(OutputPOJO outputSettings) {
        this.outputSettings = outputSettings;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public ProjectStatusForUIProjectSettingsPagePOJO getStatus() {
        return status;
    }

    public void setStatus(ProjectStatusForUIProjectSettingsPagePOJO status) {
        this.status = status;
    }

    public ActionsPOJO getActions() {
        return actions;
    }

    public void setActions(ActionsPOJO actions) {
        this.actions = actions;
    }


}
