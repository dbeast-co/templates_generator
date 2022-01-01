package com.dbeast.templates_generator.templates_generator.pojo.ui_pojo.project_settings;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InputSettingsPOJO implements Cloneable {
    @JsonProperty("index_for_analyze")
    private String indexForAnalyze;
    @JsonProperty("max_docs_for_analyze")
    private long maxDocsForAnalyze = 100000;
    @JsonProperty("scroll_size")
    private int scrollSize = 1000;

    @Override
    public InputSettingsPOJO clone() {
        InputSettingsPOJO result = null;
        try {
            result = (InputSettingsPOJO) super.clone();
            return result;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return this;
        }
    }

    public String getIndexForAnalyze() {
        return indexForAnalyze;
    }

    public void setIndexForAnalyze(String indexForAnalyze) {
        if (indexForAnalyze != null && !indexForAnalyze.isEmpty()) {
            this.indexForAnalyze = indexForAnalyze.trim();
        } else {
            this.indexForAnalyze = indexForAnalyze;
        }
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

}
