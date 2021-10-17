package com.dbeast.templates_generator.templates_generator.pojo.ui_pojo.project_settings;

import org.elasticsearch.search.SearchHit;

import java.util.LinkedList;
import java.util.List;

public class ScrollResultsPOJO {
    private String scrollId;
    private List<SearchHit> results = new LinkedList<>();

    public String getScrollId() {
        return scrollId;
    }

    public void setScrollId(String scrollId) {
        this.scrollId = scrollId;
    }

    public List<SearchHit> getResults() {
        return results;
    }

    public void setResults(List<SearchHit> results) {
        this.results = results;
    }
}
