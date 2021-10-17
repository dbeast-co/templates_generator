package com.dbeast.templates_generator.projects_monitoring;

import com.dbeast.templates_generator.constants.EProjectStatus;
import com.dbeast.templates_generator.templates_generator.pojo.ui_pojo.project_settings.ProjectPOJO;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ProjectStatusForUIProjectsMonitoringPOJO {
    @JsonProperty("project_id")
    private String projectId;
    @JsonProperty("project_name")
    private String projectName;
    @JsonProperty("execution_progress")
    private int executionProgress;
    @JsonProperty("project_status")
    private EProjectStatus projectStatus = EProjectStatus.NEW;
    @JsonProperty("docs_in_index")
    private long docsInIndex;
    @JsonProperty("docs_for_analyze")
    private long docsForAnalyze;
    @JsonProperty("analyzed_docs")
    private long analyzedDocs;
    @JsonProperty("start_time")
    private String startTime;
    @JsonProperty("end_time")
    private String endTime;

    public ProjectStatusForUIProjectsMonitoringPOJO(ProjectPOJO project) {
        this.projectId = project.getProjectId();
        this.projectName = project.getProjectName();
        this.executionProgress = project.getStatus().getExecutionProgress();
        this.projectStatus = project.getStatus().getProjectStatus();
        this.docsInIndex = project.getStatus().getDocsInIndex();
        this.docsForAnalyze = project.getStatus().getDocsForAnalyze();
        this.analyzedDocs = project.getStatus().getAnalyzedDocs();
        this.startTime = project.getStatus().getStartTime();
        this.endTime = project.getStatus().getEndTime();
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public int getExecutionProgress() {
        return executionProgress;
    }

    public void setExecutionProgress(int executionProgress) {
        this.executionProgress = executionProgress;
    }

    public EProjectStatus getProjectStatus() {
        return projectStatus;
    }

    public void setProjectStatus(EProjectStatus projectStatus) {
        this.projectStatus = projectStatus;
    }

    public long getDocsInIndex() {
        return docsInIndex;
    }

    public void setDocsInIndex(long docsInIndex) {
        this.docsInIndex = docsInIndex;
    }

    public long getDocsForAnalyze() {
        return docsForAnalyze;
    }

    public void setDocsForAnalyze(long docsForAnalyze) {
        this.docsForAnalyze = docsForAnalyze;
    }

    public long getAnalyzedDocs() {
        return analyzedDocs;
    }

    public void setAnalyzedDocs(long analyzedDocs) {
        this.analyzedDocs = analyzedDocs;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
