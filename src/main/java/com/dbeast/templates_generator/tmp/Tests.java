//package com.dbeast.index_analyzer.tmp;
//
//import com.dbeast.index_analyzer.templates_generator.pojo.DateFormatsPOJO;
//import com.dbeast.index_analyzer.templates_generator.pojo.EsSettingsPOJO;
//import com.dbeast.index_analyzer.templates_generator.pojo.cli_pojo.CLIProjectPOJO;
//import com.dbeast.index_analyzer.templates_generator.MappingGenerator;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Map;
//
//
//public class Tests {
//    private static final Logger logger = LogManager.getLogger();
//    private static final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
//    private static final ObjectMapper jsonMapper = new ObjectMapper();
//    private static final CLIProjectPOJO appConfiguration = new CLIProjectPOJO();
//    private static List<DateFormatsPOJO> dateFormats = new LinkedList<>();
//
//    static MappingGenerator mappingGenerator;
//
//    public static void main(String[] args) {
//
//
//        String input = "input {\n" +
//                "twitter {\n" +
//                "keywords => [\"apples\",\"bananas\",\"cherries\"]\n" +
//                "follows => [\"@sirwebber\"]\n" +
//                "}\n" +
//                "}\n" +
//                "\n" +
//                "output {\n" +
//                "elasticsearch {\n" +
//                "something =>\n" +
//                "[\"hello\"]\n" +
//                "}\n" +
//                "}";
//
//
//        String res = input.replaceAll("\n", " ")
////                .replaceAll("=>", ":")
//                .replace("input", "\"input\" :")
//                .replace("output", "\"output\" :")
//                .replace("filter", "\"filter\" :");
//        char[] charInput = res.toCharArray();
//        for (int i =0; i<charInput.length; i++){
//
//        }
//
//        try {
//            JsonNode applicationResponse = jsonMapper.readTree(res);
//            System.out.println(applicationResponse);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
//
//
////        initEsSettings();
////        initDateFormats();
////        mappingGenerator = new MappingGenerator(dateFormats);
////        generateDateFromDoubleObjectWithWrongNumberOfChars();
//    }
//
//    static void generateDateFromDoubleObject() {
//        logger.info("Generate one value inner object test");
//        String DOC = "{\n" +
//                "  \"date\" : \"1620029380.668\"\n" +
//                "}";
//        String RESPONSE = "{\"mappings\":{\"properties\":{\"date\":{\"type\":\"date\"}}}}";
//        runAndCompare(DOC, RESPONSE);
//    }
//
//
//    static void generateDateFromDoubleObjectWithWrongNumberOfChars() {
//        logger.info("Generate one value inner object test");
//        String DOC = "{\n" +
//                "  \"date\" : \"1620029380.6681111\"\n" +
//                "}";
//        String RESPONSE = "{\"mappings\":{\"properties\":{\"date\":{\"type\":\"double\"}}}}";
//        runAndCompare(DOC, RESPONSE);
//    }
//
////
////    private static void runAndCompare(final String DOC, final String RESPONSE) {
////        mappingGenerator.generate(DOC);
////        try {
////            Map<String, Object> result = mappingGenerator.getSchema();
////            JsonNode expectedResponse = jsonMapper.readTree(RESPONSE);
////            JsonNode applicationResponse = jsonMapper.readTree(objectToString(result));
//////            assertEquals(expectedResponse, applicationResponse);
////            logger.info(objectToString(result));
////        } catch (JsonProcessingException e) {
////            e.printStackTrace();
////        }
////    }
//
//
//    private static String objectToString(Map<String, Object> object) throws JsonProcessingException {
//        return "\n" + jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
//    }
//
//
//    private static void initEsSettings() {
//        EsSettingsPOJO esSettings = new EsSettingsPOJO();
//        esSettings.setEs_host("http://localhost:9200");
//        appConfiguration.getInputSettings().setEsSettings(esSettings);
//
//    }
//
//    private static void initDateFormats() {
//        try {
//            TypeReference<List<DateFormatsPOJO>> typeRef = new TypeReference<List<DateFormatsPOJO>>() {
//            };
//            dateFormats = mapper.readValue(new File(
//                            "C:\\Users\\umssiaq\\Desktop\\Vakhtang\\index_analyzer_source\\index_analyzer_source\\out\\config\\date_formats.yml"),
//                    typeRef);
//        } catch (IOException e) {
//            logger.error("Error: " + e);
//            System.exit(-1);
//        }
//    }
//}
