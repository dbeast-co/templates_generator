//package com.electusplus.index_analyzer.data_warehouse;
//
//import com.electusplus.index_analyzer.exceptions.ClusterConnectionException;
//import com.electusplus.index_analyzer.mapping_generator.MappingGenerator;
//import com.electusplus.index_analyzer.mapping_generator.pojo.cli_pojo.ProjectPOJO;
//
//import java.io.IOException;
//
//public class ProjectsController {
//    public void analyzeIndex(String projectId) {
//        MappingGenerator mappingGenerator = new MappingGenerator(null);
//        try {
//            mappingGenerator.generate(new ProjectPOJO());
//        } catch (ClusterConnectionException | IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    public ProjectPOJO getNewProject(){
//        return new ProjectPOJO();
//    }
//}
