package com.dbeast.templates_generator.rest;

import com.dbeast.templates_generator.data_warehouse.DataWarehouse;
import com.dbeast.templates_generator.data_warehouse.MappingGeneratorController;
import com.dbeast.templates_generator.exceptions.ClusterConnectionException;
import com.dbeast.templates_generator.exceptions.IndexNotFoundOrEmptyException;
import com.dbeast.templates_generator.exceptions.TemplateNotFoundException;
import com.dbeast.templates_generator.templates_generator.pojo.EsSettingsPOJO;
import com.dbeast.templates_generator.templates_generator.pojo.ui_pojo.project_settings.ProjectPOJO;
import org.apache.logging.log4j.LogManager;

import org.apache.logging.log4j.Logger;
import spark.Response;

import java.io.IOException;
import java.util.HashMap;

import static spark.Spark.*;

public class RESTIndexAnalyzerSettings extends ARest {
    private static final Logger logger = LogManager.getLogger();
    private final DataWarehouse dataWarehouse = DataWarehouse.getInstance();
    private final MappingGeneratorController mappingGeneratorController = MappingGeneratorController.getInstance();

    //TODO update response for error
    public void rest() {
        path("/project_settings", () -> {

            get("/new", (request, response) -> {
                logger.info("Got request for new project!");
                return objectToString(dataWarehouse.getNewProject());
            });
            get("/get/:id", (request, response) -> {
                logger.info("Got request for project with id: " + request.params(":id"));
                ProjectPOJO result = mappingGeneratorController.getProjectById(request.params(":id"));
                return objectToString(result);
            });
            get("/start/:id", (request, response) -> {
                logger.info("Got request for run project with id: " + request.params(":id"));
                try {
                    mappingGeneratorController.runProject(request.params(":id"));
                    return true;
                } catch (IndexNotFoundOrEmptyException | TemplateNotFoundException | ClusterConnectionException | IOException | RuntimeException e) {
                    response.status(406);
                    return e.getMessage();
                }
            });
            get("/stop/:id", (request, response) -> {
                logger.info("Got request for stop project with id: " + request.params(":id"));
                mappingGeneratorController.stopProject(request.params(":id"));
                return true;
            });
            get("/list", (request, response) -> {
                logger.info("Got requests for project list");
                return objectToString(mappingGeneratorController.getProjectsList());
            });
            get("/validate/:projectName", (request, response) -> {
                logger.info("Got request for validate project name: " + request.params("projectName"));
                return objectToString(mappingGeneratorController.validateIsProjectNameExists(request.params("projectName")));
            });
            get("/validate/", (request, response) -> {
                return false;
            });
            get("/download_template/:id", (request, response) -> {
                logger.info("Got request for download template file!");
                setCorsHeaders(response);
                return mappingGeneratorController.getFile(response, request.params(":id"), "template");
            });
            get("/download_index/:id", (request, response) -> {
                logger.info("Got request for download index file!");
                setCorsHeaders(response);
                return mappingGeneratorController.getFile(response, request.params(":id"), "index");
            });
            get("/download_change_log/:id", (request, response) -> {
                logger.info("Got request for download mapping change log file!");
                setCorsHeaders(response);
                return mappingGeneratorController.getFile(response, request.params(":id"), "change_log");
            });
            get("/download_all/:id", (request, response) -> {
                logger.info("Got request for download all files file!");
                setCorsHeaders(response);
                return mappingGeneratorController.getFile(response, request.params(":id"), "all");
            });

            post("/start", (request, response) -> {
                ProjectPOJO project = mapper.readValue(request.body(), ProjectPOJO.class);
                logger.info("Got request for run project with id: " + project.getProjectId());
                try {
                    return mappingGeneratorController.runProject(project);
                } catch (IndexNotFoundOrEmptyException | TemplateNotFoundException e) {
                    response.status(406);
                    return e.getMessage();
                }

            });

            post("/test_cluster/:id", (request, response) -> {
                logger.info("Got request for test Elasticsearch server with id: " + request.params(":id"));
                if (logger.isDebugEnabled()) {
                    logger.debug("Request body: " + request.body());
                }
                EsSettingsPOJO connectionSettings = mapper.readValue(request.body(), EsSettingsPOJO.class);
                String responseBody = mappingGeneratorController.getClusterStatus(connectionSettings, request.params(":id"));
                if (responseBody.contains("error")) {
                    response.status(406);
                }
                return responseBody;
            });
            post("/save", (request, response) -> {
                logger.info("Got request for save project");
                if (logger.isDebugEnabled()) {
                    logger.debug("Request body: " + request.body());
                }
                ProjectPOJO project = mapper.readValue(request.body(), ProjectPOJO.class);
                return mappingGeneratorController.saveProject(project);
            });
            post("/ssl_cert/:usage/:projectId", (request, response) -> {
                logger.info("Got request for upload SSL certificate for project with id: " + request.params(":projectId"));
//                setCorsHeaders(response);
                boolean result = mappingGeneratorController.uploadSSLCert(request);
                return result;
            });
            delete("/delete/:id", (request, response) -> {
                logger.info("Got request for delete project with Id: " + request.params(":id"));
                return mappingGeneratorController.deleteProjectById(request.params(":id"));
            });

        });
    }

    private void setCorsHeaders(Response response) {
        HashMap<String, String> corsHeaders = new HashMap<>();
        corsHeaders.put("Access-Control-Allow-Methods", "GET,PUT,POST,DELETE");
        corsHeaders.put("Access-Control-Allow-Origin", "*");
        corsHeaders.put("Access-Control-Allow-Headers", "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin,Content-Disposition," +
                "Accept-Encoding,Accept-Language,Sec-Fetch-Dest,Sec-Fetch-Site,Sec-Fetch-Mode,Connection,Referer,User-Agent,Host" +
                "origin, content-type, cache-control, accept, options, authorization, x-requested-with");
        corsHeaders.put("Access-Control-Allow-Credentials", "true");
        corsHeaders.put("Access-Control-Expose-Headers", "Content-Disposition");
        corsHeaders.forEach(response::header);
    }
}
