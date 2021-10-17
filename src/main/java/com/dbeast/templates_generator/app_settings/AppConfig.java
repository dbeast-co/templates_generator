package com.dbeast.templates_generator.app_settings;

import com.dbeast.templates_generator.templates_generator.pojo.DateFormatsPOJO;

import java.util.List;

public class AppConfig {
    public static String homeFolder;
    public static String configFolder;
    public static String projectsFolder;
    public static List<DateFormatsPOJO> dateFormats;
    public static boolean isDebugMode;
    public static String fieldForTest;

    public AppConfig(final String homeFolder,
                     final String configFolder,
                     final String projectsFolder,
                     final List<DateFormatsPOJO> dateFormats,
                     final boolean isDebugMode,
                     final String fieldForTest) {
        AppConfig.homeFolder = homeFolder;
        AppConfig.configFolder = configFolder;
        AppConfig.projectsFolder = projectsFolder;
        AppConfig.dateFormats = dateFormats;
        AppConfig.isDebugMode = isDebugMode;
        AppConfig.fieldForTest = fieldForTest;
    }
}
