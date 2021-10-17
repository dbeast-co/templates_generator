package com.dbeast.templates_generator.data_warehouse;

import com.dbeast.templates_generator.TemplatesGenerator;
import com.dbeast.templates_generator.utils.GeneralUtils;
import com.dbeast.templates_generator.app_settings.AppSettingsPOJO;
import com.dbeast.templates_generator.templates_generator.pojo.ui_pojo.project_settings.ProjectPOJO;
import com.dbeast.templates_generator.constants.EAppSettings;
import com.dbeast.templates_generator.templates_generator.MappingGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataWarehouse {
    private static final Logger logger = LogManager.getLogger();
    private MappingGenerator mappingGenerator;
    private Map<String, ProjectPOJO> projectsMap = new HashMap<>();
    private Map<String, List<String>> projectAnalyticsLog = new HashMap<>();
    private Map<String, Map<String, Object>> projectsSamples = new HashMap<>();

    private static final DataWarehouse _instance = new DataWarehouse();
    private AppSettingsPOJO appSettings;

    public static synchronized DataWarehouse getInstance() {
        if (_instance == null) {
            return new DataWarehouse();
        }
        return _instance;
    }

    private DataWarehouse() {
    }

    public void init() {
        boolean isDone = GeneralUtils.createFolderIfNotExists(TemplatesGenerator.projectsFolder);
        if (!isDone) {
            System.exit(-1);
        }
    }

    public void init(AppSettingsPOJO appSettings) {
        boolean isDone = GeneralUtils.createFolderIfNotExists(TemplatesGenerator.projectsFolder);
        if (!isDone) {
            System.exit(-1);
        }
        this.appSettings = appSettings;
        List<Path> filesList = GeneralUtils.readFilesFromFolderPathOneInnerFolder(TemplatesGenerator.projectsFolder);
        filesList.forEach(this::readExistingProject);
    }

    public ProjectPOJO getNewProject() {
        return new ProjectPOJO();
    }


    public Map<String, ProjectPOJO> getProjectsMap() {
        return projectsMap;
    }

    public void setProjectsMap(Map<String, ProjectPOJO> projectsMap) {
        this.projectsMap = projectsMap;
    }

    public Map<String, List<String>> getProjectAnalyticsLog() {
        return projectAnalyticsLog;
    }

    public void setProjectAnalyticsLog(Map<String, List<String>> projectAnalyticsLog) {
        this.projectAnalyticsLog = projectAnalyticsLog;
    }

    public Map<String, Map<String, Object>> getProjectsSamples() {
        return projectsSamples;
    }

    public void setProjectsSamples(Map<String, Map<String, Object>> projectsSamples) {
        this.projectsSamples = projectsSamples;
    }

    public AppSettingsPOJO getAppSettings() {
        return appSettings;
    }

    public void setAppSettings(AppSettingsPOJO appSettings) {
        this.appSettings = appSettings;
    }

    private void readExistingProject(Path file) {
        if (file.toString().contains(EAppSettings.PROJECT_SETTINGS_FILE.getConfigurationParameter())) {
            ProjectPOJO project = GeneralUtils.readFileFromFileAndSerializeToObject(file, ProjectPOJO.class);
            if (project != null) {
                projectsMap.put(project.getProjectId(), project);
            }
        }
    }
}
