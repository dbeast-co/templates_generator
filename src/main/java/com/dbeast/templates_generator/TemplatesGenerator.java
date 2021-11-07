package com.dbeast.templates_generator;

import com.dbeast.templates_generator.app_settings.AppPOJO;
import com.dbeast.templates_generator.app_settings.AppSettingsPOJO;
import com.dbeast.templates_generator.constants.EAppSettings;
import com.dbeast.templates_generator.data_warehouse.DataWarehouse;
import com.dbeast.templates_generator.exceptions.ClusterConnectionException;
import com.dbeast.templates_generator.exceptions.IndexNotFoundOrEmptyException;
import com.dbeast.templates_generator.exceptions.TemplateNotFoundException;
import com.dbeast.templates_generator.templates_generator.MappingGenerator;
import com.dbeast.templates_generator.templates_generator.pojo.DateFormatsPOJO;
import com.dbeast.templates_generator.templates_generator.pojo.cli_pojo.CLIProjectPOJO;
import com.dbeast.templates_generator.templates_generator.pojo.ui_pojo.project_settings.ProjectPOJO;
import com.dbeast.templates_generator.rest.MainRest;
import com.dbeast.templates_generator.utils.GeneralUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class TemplatesGenerator {
    private static final Logger logger = LogManager.getLogger();
    public static String homeFolder;
    public static String configFolder;
    public static String projectsFolder;
    public static List<DateFormatsPOJO> dateFormats;
    public static boolean isDebugMode = false;
    public static String fieldForTest = "DevicePropertyDeviceDescription";

    public TemplatesGenerator() {
    }

    public static void main(String[] args) throws Exception {
        logger.info("Welcome to Index analyzer application");
        if (args.length > 1) {
            runCLI(args);
        } else {
            runServer(args);
        }
    }

    private static void runServer(String[] args) throws Exception {
        homeFolder = args[0];
        configFolder = homeFolder + EAppSettings.CONFIG_FOLDER.getConfigurationParameter();
        projectsFolder = homeFolder + EAppSettings.PROJECTS_FOLDER.getConfigurationParameter();
        logger.info("Home folder: " + homeFolder);
        logger.info("Configuration folder: " + configFolder);
        logger.info("Projects folder: " + projectsFolder);
        logger.info("Application home folder: " + homeFolder);
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

        AppSettingsPOJO appSettings;
        try {
            File configFile = new File(homeFolder +
                    EAppSettings.CONFIG_FOLDER.getConfigurationParameter() +
                    EAppSettings.CONFIGURATION_FILE.getConfigurationParameter());
            AppPOJO appConfiguration = mapper.readValue(configFile, AppPOJO.class);
            appSettings = new AppSettingsPOJO(appConfiguration, homeFolder);
        } catch (IOException e) {
            throw new Exception("Configuration folder not present or Incorrect configuration file" + e);
        }
        try {
            TypeReference<List<DateFormatsPOJO>> typeRef = new TypeReference<List<DateFormatsPOJO>>() {
            };
            dateFormats = mapper.readValue(new File(configFolder + EAppSettings.DATE_FORMATS_FILE.getConfigurationParameter()), typeRef);
        } catch (IOException e) {
            logger.error("Error: " + e);
            System.exit(-1);
        }
        DataWarehouse dataWarehouse = DataWarehouse.getInstance();
        dataWarehouse.init(appSettings);
        MainRest restApi = new MainRest(appSettings);
        restApi.runServer(appSettings.getApp().getHost(), appSettings.getApp().getPort());
    }

    public static void stop() {
        System.exit(0);
    }

    public static void runCLI(String[] args) throws ClusterConnectionException, IOException, IndexNotFoundOrEmptyException, TemplateNotFoundException {
        final String fileForProcessing = args[0];
        homeFolder = args[1];
        String configFolder = homeFolder + EAppSettings.CONFIG_FOLDER.getConfigurationParameter();
        projectsFolder = homeFolder + EAppSettings.PROJECTS_FOLDER.getConfigurationParameter();
        logger.info("Home folder: " + homeFolder);
        logger.info("Configuration folder: " + configFolder);
        logger.info("Projects folder: " + projectsFolder);

        if (fileForProcessing == null) {
            logger.error("The configuration file doesn't specified ");
            System.exit(-1);
        } else {
            logger.info("File for processing: " + fileForProcessing);
        }
        GeneralUtils.createFolderIfNotExists(projectsFolder);
        DataWarehouse dataWarehouse = DataWarehouse.getInstance();
        dataWarehouse.init();

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        CLIProjectPOJO projectConfiguration = null;
        try {
            projectConfiguration = mapper.readValue(new File(fileForProcessing), CLIProjectPOJO.class);
            TypeReference<List<DateFormatsPOJO>> typeRef = new TypeReference<List<DateFormatsPOJO>>() {
            };
            dateFormats = mapper.readValue(new File(configFolder + EAppSettings.DATE_FORMATS_FILE.getConfigurationParameter()), typeRef);
        } catch (IOException e) {
            logger.error("Incorrect configuration file: " + fileForProcessing);
            logger.error("Error: " + e);
            System.exit(-1);
        }
        MappingGenerator mappingGenerator = null;
        mappingGenerator = new MappingGenerator(dateFormats,
                new ProjectPOJO(projectConfiguration));

        //TODO add validation for conf file (index true but properties not exists e t.c.)
        boolean result = mappingGenerator.generate();
        if (result) {
            System.exit(0);
        } else {
            System.exit(-1);
        }
    }
}
