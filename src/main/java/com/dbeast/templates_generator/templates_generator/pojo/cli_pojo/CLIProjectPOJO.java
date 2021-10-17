package com.dbeast.templates_generator.templates_generator.pojo.cli_pojo;

import com.dbeast.templates_generator.utils.GeneralUtils;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CLIProjectPOJO {
    @JsonProperty("project_name")
    private String projectName;
    @JsonProperty("project_id")
    private String projectId = GeneralUtils.generateNewUID();
    @JsonProperty("input")
    private CLIInputPOJO inputSettings = new CLIInputPOJO();
    @JsonProperty("output")
    private CLIOutputPOJO outputSettings = new CLIOutputPOJO();

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public CLIInputPOJO getInputSettings() {
        return inputSettings;
    }

    public void setInputSettings(CLIInputPOJO inputSettings) {
        this.inputSettings = inputSettings;
    }

    public CLIOutputPOJO getOutputSettings() {
        return outputSettings;
    }

    public void setOutputSettings(CLIOutputPOJO outputSettings) {
        this.outputSettings = outputSettings;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
}
