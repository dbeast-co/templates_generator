package com.dbeast.templates_generator.templates_generator.pojo.ui_pojo.project_settings;

import com.dbeast.templates_generator.templates_generator.pojo.EsSettingsPOJO;
import com.fasterxml.jackson.annotation.JsonProperty;

public class InputPOJO implements Cloneable{
    @JsonProperty("input_settings")
    private InputSettingsPOJO inputSettings = new InputSettingsPOJO();
    @JsonProperty("source_cluster")
    private EsSettingsPOJO sourceCluster = new EsSettingsPOJO();

    @Override
    public InputPOJO clone() {
        InputPOJO result = null;
        try {
            result = (InputPOJO) super.clone();
            result.inputSettings = inputSettings.clone();
            result.sourceCluster = sourceCluster.clone();
            return result;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return this;
        }
    }


    public InputSettingsPOJO getInputSettings() {
        return inputSettings;
    }

    public void setInputSettings(InputSettingsPOJO inputSettings) {
        this.inputSettings = inputSettings;
    }

    public EsSettingsPOJO getSourceCluster() {
        return sourceCluster;
    }

    public void setSourceCluster(EsSettingsPOJO sourceCluster) {
        this.sourceCluster = sourceCluster;
    }
}
