package com.dbeast.templates_generator.app_settings;

public class AppSettingsPOJO {
    private AppPOJO app;
    private final InternalSettings internals;

    public AppSettingsPOJO(AppPOJO app, final String homeFolder) {
        this.app = app;
        this.internals = new InternalSettings(homeFolder);
    }

    public AppPOJO getApp() {
        return app;
    }

    public void setApp(AppPOJO app) {
        this.app = app;
    }
}
