package com.dbeast.templates_generator.templates_generator;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.stream.Collectors;

public class DataAnalyzer {
    private static final Logger logger = LogManager.getLogger();

    public void analyze() {
        List<String> rawData = readFile("");
        List<JsonNode> objectsList = rawData.stream()
                .map(this::dataToJsonObjects)
                .collect(Collectors.toList());

    }

    private JsonNode dataToJsonObjects(String raw) {
        return null;
    }

    private List<String> readFile(final String file) {
        return null;
    }



//    private void updateSchema(Map<String, Object> schema, JsonNode actualObj) {
//        addToSchema("mapping", actualObj, schema);
//    }

//    private void addToSchema(final String key, final JsonNode jsonNode, final Map<String, Object> schema) {
//        if (jsonNode.isObject()) {
//            ObjectNode objectNode = (ObjectNode) jsonNode;
//            HashMap<String, Object> newObject = new HashMap<>();
//            schema.put(key, newObject);
//            Iterator<Map.Entry<String, JsonNode>> iter = objectNode.fields();
//
//            while (iter.hasNext()) {
//                Map.Entry<String, JsonNode> entry = iter.next();
//                addToSchema(entry.getKey(), entry.getValue(), newObject);
//            }
//        } else if (jsonNode.isValueNode()) {
//            ValueNode valueNode = (ValueNode) jsonNode;
//            valueNode.
//            map.put(currentPath, valueNode.asText());
//        }
//    }

//    private void addKeys(String currentPath, JsonNode jsonNode, Map<String, String> map, List<Integer> suffix) {
//        if (jsonNode.isObject()) {
//            ObjectNode objectNode = (ObjectNode) jsonNode;
//            if (schema.containsKey())
//                Iterator<Map.Entry<String, JsonNode>> iter = objectNode.fields();
//            String pathPrefix = currentPath.isEmpty() ? "" : currentPath + "-";
//
//            while (iter.hasNext()) {
//                Map.Entry<String, JsonNode> entry = iter.next();
//                addKeys(pathPrefix + entry.getKey(), entry.getValue(), map, suffix);
//            }
//        } else if (jsonNode.isArray()) {
//            ArrayNode arrayNode = (ArrayNode) jsonNode;
//
//            for (int i = 0; i < arrayNode.size(); i++) {
//                suffix.add(i + 1);
//                addKeys(currentPath, arrayNode.get(i), map, suffix);
//
//                if (i + 1 < arrayNode.size()) {
//                    suffix.remove(arrayNode.size() - 1);
//                }
//            }
//
//        } else if (jsonNode.isValueNode()) {
//            if (currentPath.contains("-")) {
//                for (int i = 0; i < suffix.size(); i++) {
//                    currentPath += "-" + suffix.get(i);
//                }
//
//                suffix = new ArrayList<>();
//            }
//
//            ValueNode valueNode = (ValueNode) jsonNode;
//            map.put(currentPath, valueNode.asText());
//        }
//    }

    private void analyzeField() {

    }
}
