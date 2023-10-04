package com.dbeast.templates_generator.rest;

import com.dbeast.templates_generator.data_warehouse.DataWarehouse;
import com.dbeast.templates_generator.data_warehouse.MappingGeneratorController;
import com.dbeast.templates_generator.exceptions.ClusterConnectionException;
import com.dbeast.templates_generator.exceptions.IndexNotFoundOrEmptyException;
import com.dbeast.templates_generator.exceptions.TemplateNotFoundException;
import com.dbeast.templates_generator.legacy_to_index_templates_converter.LegacyToIndexTemplatesConverter;
import com.dbeast.templates_generator.legacy_to_index_templates_converter.TemplateConverterSettingsPOJO;
import com.dbeast.templates_generator.templates_generator.pojo.EsSettingsPOJO;
import com.dbeast.templates_generator.templates_generator.pojo.ui_pojo.project_settings.ProjectPOJO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.index.OneMergeWrappingMergePolicy;
import spark.Response;

import java.io.IOException;
import java.util.HashMap;

import static spark.Spark.*;

public class RESTLegacyToIndexTemplateSettings extends ARest {
    private static final Logger logger = LogManager.getLogger();

    private final LegacyToIndexTemplatesConverter legacyToIndexTemplatesConverter = LegacyToIndexTemplatesConverter.getInstance();

    private final MappingGeneratorController mappingGeneratorController = MappingGeneratorController.getInstance();

    public void rest() {
        path("/", () -> {

            post("/run", (request, response) -> {
                TemplateConverterSettingsPOJO project = mapper.readValue(request.body(), TemplateConverterSettingsPOJO.class);
                logger.info("Got request for transform templates");
                try {
                    response = legacyToIndexTemplatesConverter.runProject(response, project);
                    setCorsHeaders(response);
                    return response;
                } catch (Exception e) {
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

            post("/get_templates", (request, response) -> {
                if (logger.isDebugEnabled()) {
                    logger.debug("Got request for get sources from Elasticsearch");
                    logger.debug("Request body: " + request.body());
                }
                TemplateConverterSettingsPOJO project = mapper.readValue(request.body(), TemplateConverterSettingsPOJO.class);
                legacyToIndexTemplatesConverter.getTemplatesList(project);
                return objectToString(project);
            });
//            post("/ssl_cert/:usage/:projectId", (request, response) -> {
//                logger.info("Got request for upload SSL certificate for project with id: " + request.params(":projectId"));
////                setCorsHeaders(response);
//                boolean result = mappingGeneratorController.uploadSSLCert(request);
//                return result;
//            });

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

//            get("/new", (request, response) -> {
//                logger.info("Got request for new project!");
//                return objectToString(dataWarehouse.getNewProject());
//            });
//            get("/get/:id", (request, response) -> {
//                if (logger.isDebugEnabled()) {
//                    logger.debug("Got request for project with id: " + request.params(":id"));
//                }
//                ProjectPOJO result = mappingGeneratorController.getProjectById(request.params(":id"));
//                return objectToString(result);
//            });
//            get("/start/:id", (request, response) -> {
//                logger.info("Got request for run project with id: " + request.params(":id"));
//                try {
//                    mappingGeneratorController.runProject(request.params(":id"));
//                    return true;
//                } catch (IndexNotFoundOrEmptyException | TemplateNotFoundException | ClusterConnectionException | IOException | RuntimeException e) {
//                    response.status(406);
//                    return e.getMessage();
//                }
//            });

//            get("/download_template/:id", (request, response) -> {
//                logger.info("Got request for download template file!");
//                setCorsHeaders(response);
//                return mappingGeneratorController.getFile(response, request.params(":id"), "template");
//            });
//            get("/download_index/:id", (request, response) -> {
//                logger.info("Got request for download index file!");
//                setCorsHeaders(response);
//                return mappingGeneratorController.getFile(response, request.params(":id"), "index");
//            });
//            get("/download_change_log/:id", (request, response) -> {
//                logger.info("Got request for download mapping change log file!");
//                setCorsHeaders(response);
//                return mappingGeneratorController.getFile(response, request.params(":id"), "change_log");
//            });
//            get("/download_all/:id", (request, response) -> {
//                logger.info("Got request for download all files file!");
//                setCorsHeaders(response);
//                return mappingGeneratorController.getFile(response, request.params(":id"), "all");
//            });
//            post("/save", (request, response) -> {
//                logger.info("Got request for save project");
//                if (logger.isDebugEnabled()) {
//                    logger.debug("Request body: " + request.body());
//                }
//                ProjectPOJO project = mapper.readValue(request.body(), ProjectPOJO.class);
//                return mappingGeneratorController.saveProject(project);
//            });