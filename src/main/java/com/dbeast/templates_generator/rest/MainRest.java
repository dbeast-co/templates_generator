package com.dbeast.templates_generator.rest;

import com.dbeast.templates_generator.TemplatesGenerator;
import com.dbeast.templates_generator.app_settings.AppSettingsPOJO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spark.Filter;

import java.util.HashMap;

import static spark.Spark.*;

public class MainRest {
    private static final Logger logger = LogManager.getLogger();
    private static final HashMap<String, String> corsHeaders = new HashMap<>();
    private final AppSettingsPOJO appSettings;

    static {
        corsHeaders.put("Access-Control-Allow-Methods", "GET,PUT,POST,DELETE");
        corsHeaders.put("Access-Control-Allow-Origin", "*");
        corsHeaders.put("Access-Control-Allow-Headers", "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin,Content-Disposition," +
                "Accept-Encoding,Accept-Language,Sec-Fetch-Dest,Sec-Fetch-Site,Sec-Fetch-Mode,Connection,Referer,User-Agent,Host");
        corsHeaders.put("Access-Control-Allow-Credentials", "true");
    }


    public MainRest(final AppSettingsPOJO appSettings) {
        this.appSettings = appSettings;
    }

    public void runServer(final String host, final int port) throws Exception {
        try {
            logger.info("Server Host: " + host +
                    " Port: " + port);
            port(port);
            ipAddress(host);
            staticFiles.externalLocation(TemplatesGenerator.homeFolder + "/client");
            initServerSettings();
            initRestAPIs();
        } catch (Exception e) {
            throw new Exception("The problem in server running! Exception: " + e);
        }
    }

    /**
     * Initialize all REST APIs
     */
    private void initRestAPIs() {
        RESTIndexAnalyzerSettings restIndexAnalyzerSettings = new RESTIndexAnalyzerSettings();
        RESTProjectsMonitoring restProjectsMonitoring = new RESTProjectsMonitoring();
        path("/index_analyzer", () -> {
            restIndexAnalyzerSettings.rest();
            restProjectsMonitoring.rest();
        });
    }

    /**
     * Initialize REST server settings
     */
    private void initServerSettings() {

//        int maxThreads = 8;
//        int minThreads = 2;
//        int timeOutMillis = 30000;
//        threadPool(maxThreads, minThreads, timeOutMillis);
        /**
         * Initialize Internal exception handler
         */
        initExceptionHandler((e) -> {
            logger.error("Error while running REST server! Exception: " + e);
            if (e.getCause().getMessage().contains("Address already in use")){
                System.exit(-1);
            }
        });

        /**
         * Initialize page not found handler
         */
        notFound((request, response) -> {
            logger.warn("Got incorrect URI request from ip: " + request.ip() +
                    " Requested URI: " + request.uri() +
                    " Request body: " + request.body()
            );
            response.status(404);
            return "Page: " + request.uri() + " not found";
        });

        before((request, response) -> response.type("application/json"));
        after((request, response) -> response.type("application/json"));

        Filter filter = (request, response) -> corsHeaders.forEach(response::header);
        after(filter);
        options("/*", (request, response) -> {
            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }
            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }

            return "OK";
        });

    }


}
