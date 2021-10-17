package com.dbeast.templates_generator.rest;

import com.dbeast.templates_generator.data_warehouse.ProjectsMonitoringController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static spark.Spark.get;
import static spark.Spark.path;

public class RESTProjectsMonitoring extends ARest {
    private static final Logger logger = LogManager.getLogger();
    private final ProjectsMonitoringController projectsMonitoringController = new ProjectsMonitoringController();
    //TODO update response for error
    public void rest() {
        path("/projects_monitoring", () -> {
            get("/projects_status", (request, response) -> {
                if (logger.isDebugEnabled()) {
                    logger.debug("Got request for projects monitoring");
                }
                return objectToString(projectsMonitoringController.getProjectsStatusForUI());
            });
        });
    }
}
