package com.dbeast.templates_generator.templates_generator.pojo.cli_pojo;

import com.dbeast.templates_generator.templates_generator.pojo.ui_pojo.project_settings.InputPOJO;
import com.dbeast.templates_generator.templates_generator.pojo.EsSettingsPOJO;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CLIInputPOJO {
    @JsonProperty("index_for_analyze")
    private String indexForAnalyze;
    @JsonProperty("max_docs_for_analyze")
    private long maxDocsForAnalyze = 100000;
    @JsonProperty("scroll_size")
    private int scrollSize = 1000;
    @JsonProperty("elasticsearch")
    private EsSettingsPOJO esSettings = new EsSettingsPOJO();

    public CLIInputPOJO(InputPOJO appInput) {
        this.indexForAnalyze = appInput.getInputSettings().getIndexForAnalyze();
        this.maxDocsForAnalyze = appInput.getInputSettings().getMaxDocsForAnalyze();
        this.esSettings = appInput.getSourceCluster();
        this.scrollSize = appInput.getInputSettings().getScrollSize();
    }

    public CLIInputPOJO() {
    }

    public String getIndexForAnalyze() {
        return indexForAnalyze;
    }

    public void setIndexForAnalyze(String indexForAnalyze) {
        this.indexForAnalyze = indexForAnalyze;
    }

    public long getMaxDocsForAnalyze() {
        return maxDocsForAnalyze;
    }

    public void setMaxDocsForAnalyze(long maxDocsForAnalyze) {
        this.maxDocsForAnalyze = maxDocsForAnalyze;
    }

    public int getScrollSize() {
        return scrollSize;
    }

    public void setScrollSize(int scrollSize) {
        this.scrollSize = scrollSize;
    }

    public EsSettingsPOJO getEsSettings() {
        return esSettings;
    }

    public void setEsSettings(EsSettingsPOJO esSettings) {
        this.esSettings = esSettings;
    }
}
