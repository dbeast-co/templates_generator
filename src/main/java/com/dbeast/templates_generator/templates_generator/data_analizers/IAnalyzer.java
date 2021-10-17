package com.dbeast.templates_generator.templates_generator.data_analizers;

import java.util.Map;

public interface IAnalyzer {

    void analyze(String key, Map<String, Object> schema);

    void compareAndUpdate(String key, Map<String, Object> schema);

}
