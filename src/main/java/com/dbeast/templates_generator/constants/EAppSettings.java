package com.dbeast.templates_generator.constants;

public enum EAppSettings {
    PROJECT_SETTINGS_FILE("project_settings.json"),
    CONFIGURATION_FILE("templates_generator.yml"),
    DATE_FORMATS_FILE("date_formats.yml"),
    ANALYZER_CHANGELOG_FILE("analyzer_changelog.log"),
    ANALYZER_ALL_LOGS_FILE("all.zip"),
    ANALYZER_TEMPLATE_ZIP_FILE("template.zip"),
    PROJECTS_FOLDER("/projects/"),
    CONFIG_FOLDER("/config/"),
    CLIENT_FOLDER("/client/"),
    DEFAULT_NORMALIZER_NAME("lowercase_normalizer");

    private final String configurationParameter;

    EAppSettings(final String configurationParameter) {
        this.configurationParameter = configurationParameter;
    }

    public String getConfigurationParameter() {
        return configurationParameter;
    }
}
