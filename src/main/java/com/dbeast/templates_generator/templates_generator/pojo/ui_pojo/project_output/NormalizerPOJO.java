package com.dbeast.templates_generator.templates_generator.pojo.ui_pojo.project_output;

public class NormalizerPOJO {
    private String type = "custom";
    private String[] char_filter = new String[]{};
    private String[] filter = new String[]{"lowercase", "asciifolding"};

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String[] getChar_filter() {
        return char_filter;
    }

    public void setChar_filter(String[] char_filter) {
        this.char_filter = char_filter;
    }

    public String[] getFilter() {
        return filter;
    }

    public void setFilter(String[] filter) {
        this.filter = filter;
    }
}
