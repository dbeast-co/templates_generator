package com.dbeast.templates_generator.app_settings;


import com.dbeast.templates_generator.constants.EAppSettings;

public class InternalSettings {
    private final String projectsFolder;
    private final String clientFolder;
    private final long tasksRefreshInterval;

    public InternalSettings(String homeFolder) {
        this.projectsFolder = homeFolder + EAppSettings.PROJECTS_FOLDER.getConfigurationParameter();
        this.clientFolder = homeFolder + EAppSettings.CLIENT_FOLDER.getConfigurationParameter();
        this.tasksRefreshInterval = 10;
    }

    public String getProjectsFolder() {
        return projectsFolder;
    }

    public long getTasksRefreshInterval() {
        return tasksRefreshInterval;
    }

    public String getClientFolder() {
        return clientFolder;
    }

}