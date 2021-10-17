package com.dbeast.templates_generator.data_warehouse;

import com.dbeast.templates_generator.templates_generator.pojo.ui_pojo.project_settings.ProjectPOJO;
import com.dbeast.templates_generator.projects_monitoring.ProjectStatusForUIProjectsMonitoringPOJO;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProjectsMonitoringController {
    DataWarehouse dataWarehouse = DataWarehouse.getInstance();
    private final Map<String, ProjectPOJO> projectsMap = dataWarehouse.getProjectsMap();

    public List<ProjectStatusForUIProjectsMonitoringPOJO> getProjectsStatusForUI() {
        return projectsMap.values().stream()
                .map(ProjectStatusForUIProjectsMonitoringPOJO::new)
                .collect(Collectors.toList());
    }
}
