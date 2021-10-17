package com.dbeast.templates_generator.templates_generator.pojo.ui_pojo;

import com.dbeast.templates_generator.constants.EProjectStatus;
import com.dbeast.templates_generator.utils.GeneralUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.text.SimpleDateFormat;

public class ProjectStatusForUIProjectSettingsPagePOJO implements Cloneable {
    @JsonProperty("execution_progress")
    private int executionProgress;
    @JsonProperty("project_status")
    private EProjectStatus projectStatus = EProjectStatus.NEW;
    @JsonProperty("is_done")
    private boolean isDone = false;
    @JsonProperty("is_in_active_process")
    private boolean isInActiveProcess = false;
    @JsonProperty("is_failed")
    private boolean isFailed = false;
    @JsonProperty("is_stopped")
    private boolean isStopped = false;
    @JsonProperty("docs_in_index")
    private long docsInIndex;
    @JsonProperty("docs_for_analyze")
    private long docsForAnalyze;
    @JsonProperty("analyzed_docs")
    private long analyzedDocs;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("start_time")
    private String startTime = "";
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("end_time")
    private String endTime = "";

    public ProjectStatusForUIProjectSettingsPagePOJO() {
    }

    @Override
    public ProjectStatusForUIProjectSettingsPagePOJO clone() {
        try {
            return (ProjectStatusForUIProjectSettingsPagePOJO) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return this;
        }
    }

    public ProjectStatusForUIProjectSettingsPagePOJO(int executionProgress, EProjectStatus projectStatus, boolean isDone) {
        this.executionProgress = executionProgress;
        this.projectStatus = projectStatus;
        this.isDone = isDone;

    }

    public ProjectStatusForUIProjectSettingsPagePOJO(int executionProgress,
                                                     EProjectStatus projectStatus,
                                                     boolean isDone,
                                                     boolean isInActiveProcess) {
        this.executionProgress = executionProgress;
        this.projectStatus = projectStatus;
        this.isDone = isDone;
        this.isInActiveProcess = isInActiveProcess;

    }

    public void updateFailStatus() {
        setInActiveProcess(false);
        setProjectStatus(EProjectStatus.FAILED);
        setDone(true);
        setFailed(true);
        insertEndTimeInLong(System.currentTimeMillis());
    }


    public int getExecutionProgress() {
        return executionProgress;
    }

    public void setExecutionProgress(int executionProgress) {
        this.executionProgress = executionProgress;
    }

    public void setExecutionProgress() {
        if (docsForAnalyze > 0) {
            this.setExecutionProgress(Math.round(((analyzedDocs) * 100) / docsForAnalyze));
        } else {
            this.setExecutionProgress(0);
        }
    }

    public EProjectStatus getProjectStatus() {
        return projectStatus;
    }
//
//    public String getProjectStatus() {
//        return projectStatus.getStatusDescription();
//    }

    public void setProjectStatus(String projectStatus) {
        this.projectStatus = EProjectStatus.getByValue(projectStatus);
    }

    public void setProjectStatus(EProjectStatus projectStatus) {
        if (!this.projectStatus.equals(EProjectStatus.FAILED)) {
            this.projectStatus = projectStatus;
        }
    }

    @JsonProperty("is_done")
    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    @JsonProperty("is_in_active_process")
    public boolean isInActiveProcess() {
        return isInActiveProcess;
    }

    public void setInActiveProcess(boolean inActiveProcess) {
        isInActiveProcess = inActiveProcess;
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

    public boolean isFailed() {
        return isFailed;
    }

    @JsonProperty("is_failed")
    public void setFailed(boolean failed) {
        isFailed = failed || isFailed;
    }

    public boolean isStopped() {
        return isStopped;
    }

    @JsonProperty("is_stopped")
    public void setStopped(boolean stopped) {
        isStopped = stopped;
    }

    public String getStartTime() {
        return startTime;
    }

    public void insertStartTimeInLong(long startTime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.startTime = GeneralUtils.convertLongToDateString(startTime, dateFormat);
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

    public void insertEndTimeInLong(long endTime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.endTime = GeneralUtils.convertLongToDateString(endTime, dateFormat);
    }
}
