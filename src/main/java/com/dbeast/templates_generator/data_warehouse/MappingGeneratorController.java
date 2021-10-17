package com.dbeast.templates_generator.data_warehouse;

import com.dbeast.templates_generator.TemplatesGenerator;
import com.dbeast.templates_generator.app_settings.AppSettingsPOJO;
import com.dbeast.templates_generator.constants.EAppSettings;
import com.dbeast.templates_generator.constants.EClusterStatus;
import com.dbeast.templates_generator.elasticsearch.ElasticsearchController;
import com.dbeast.templates_generator.exceptions.ClusterConnectionException;
import com.dbeast.templates_generator.exceptions.IndexNotFoundOrEmptyException;
import com.dbeast.templates_generator.exceptions.TemplateNotFoundException;
import com.dbeast.templates_generator.templates_generator.MappingGenerator;
import com.dbeast.templates_generator.templates_generator.pojo.EsSettingsPOJO;
import com.dbeast.templates_generator.templates_generator.pojo.ui_pojo.ProjectStatusForUIProjectSettingsPagePOJO;
import com.dbeast.templates_generator.templates_generator.pojo.ui_pojo.project_settings.ProjectPOJO;
import com.dbeast.templates_generator.utils.GeneralUtils;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spark.Request;
import spark.Response;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MappingGeneratorController {
    private static final Logger logger = LogManager.getLogger();

    DataWarehouse dataWarehouse = DataWarehouse.getInstance();

    private final Map<String, ProjectPOJO> projectsMap = dataWarehouse.getProjectsMap();

    private final AppSettingsPOJO appSettings = dataWarehouse.getAppSettings();
    private final ElasticsearchController elasticsearchController = new ElasticsearchController();


    private static final MappingGeneratorController _instance = new MappingGeneratorController();

    public static MappingGeneratorController getInstance() {
        if (_instance == null) {
            return new MappingGeneratorController();
        }
        return _instance;
    }

    private MappingGeneratorController() {
    }

    public boolean saveProject(ProjectPOJO project) {
        project.setMappingChangesLog(new LinkedList<>());
        project.setStatus(new ProjectStatusForUIProjectSettingsPagePOJO());
        if (projectsMap.containsKey(project.getProjectId())) {
            projectsMap.get(project.getProjectId()).updateProject(project);
        } else {
            projectsMap.put(project.getProjectId(), project);
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, false);
        boolean res;
        res = GeneralUtils.createFolderDeleteOldIfExists(TemplatesGenerator.projectsFolder + project.getProjectId());
        res = GeneralUtils.createFile(TemplatesGenerator.projectsFolder + project.getProjectId() + "/" +
                        EAppSettings.PROJECT_SETTINGS_FILE.getConfigurationParameter(),
                projectsMap.get(project.getProjectId())) && res;
        res = GeneralUtils.createFile(TemplatesGenerator.projectsFolder + project.getProjectId() + "/" +
                        EAppSettings.ANALYZER_CHANGELOG_FILE.getConfigurationParameter(),
                "") && res;
        try {
            res = GeneralUtils.zipDirectory(TemplatesGenerator.projectsFolder + project.getProjectId(),
                    TemplatesGenerator.projectsFolder + project.getProjectId() + "/" +
                            EAppSettings.ANALYZER_ALL_LOGS_FILE.getConfigurationParameter()) && res;
        } catch (IOException e) {
            res = false;
        }
        return res;
    }

    public List<Map<String, String>> getProjectsList() {
        List<Map<String, String>> projectList = new LinkedList<>();
        projectsMap.forEach((id, project) -> {
            Map<String, String> projectSettings = new HashMap<>();
            projectSettings.put("project_id", project.getProjectId());
            projectSettings.put("project_name", project.getProjectName());
            projectList.add(projectSettings);
        });
        return projectList;
    }

    public String getClusterStatus(final EsSettingsPOJO connectionSettings, final String projectId) {
        try {
            return "{\"cluster_status\" : \"" + elasticsearchController.getClusterStatus(connectionSettings, projectId) + "\"}";
        } catch (ClusterConnectionException e) {
            logger.warn(e.getMessage());
            return "{\"cluster_status\" : \"" +
                    EClusterStatus.RED +
                    "\",\"error\":\"" +
                    e.getMessage() +
                    "\"}";
        }
    }

    public boolean runProject(final String projectId) throws IndexNotFoundOrEmptyException, TemplateNotFoundException, ClusterConnectionException, IOException, RuntimeException {
        MappingGenerator mappingGenerator = new MappingGenerator(TemplatesGenerator.dateFormats, dataWarehouse.getProjectsMap().get(projectId));
        try {
            mappingGenerator.generate();
        } catch (IndexNotFoundOrEmptyException | TemplateNotFoundException | ClusterConnectionException | IOException | RuntimeException e) {
            dataWarehouse.getProjectsMap().get(projectId).getStatus().updateFailStatus();
            throw e;
        }

        return true;
    }
//TODO transfer to project api
    public boolean runProject(final ProjectPOJO project) throws IndexNotFoundOrEmptyException, TemplateNotFoundException {
        saveProject(project);
        MappingGenerator mappingGenerator = new MappingGenerator(TemplatesGenerator.dateFormats, project);
        try {
            mappingGenerator.generate();
        } catch (ClusterConnectionException | IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public ProjectPOJO getProjectById(String projectId) {
        if (projectsMap.containsKey(projectId)) {
            return projectsMap.get(projectId);
        } else {
            ProjectPOJO project = new ProjectPOJO();
            project.setProjectId(projectId);
            return project;
        }
    }

    public void stopProject(String projectId) {
        projectsMap.get(projectId).getStatus().setStopped(true);
    }

    public boolean validateIsProjectNameExists(final String projectName) {
        List<String> projectNames = projectsMap.values().stream()
                .map(ProjectPOJO::getProjectName)
                .collect(Collectors.toList());
        return projectNames.contains(projectName);
    }

    public boolean deleteProjectById(String projectId) {
        if (projectsMap.containsKey(projectId)) {
            try {
                GeneralUtils.deleteDirectoryStream(Paths.get(TemplatesGenerator.projectsFolder + projectId));
            } catch (IOException e) {
                logger.error("There is the problem with folder delete. Folder: " +
                        TemplatesGenerator.projectsFolder + projectId);
                logger.error(e);
                return false;
            }
            projectsMap.remove(projectId);
        }
        return true;
    }

    public boolean uploadSSLCert(Request request) {
        String location = TemplatesGenerator.projectsFolder +
                request.params(":projectId") + "/";
        GeneralUtils.createFolderIfNotExists(location);
        request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement(location));
        try {
            // "file" is the key of the form data with the file itself being the value
            Part filePart = request.raw().getPart("file");

            // The name of the file user uploaded
            String uploadedFileName = filePart.getSubmittedFileName();

            InputStream stream = filePart.getInputStream();
            // Write stream to file under storage folder
            Files.copy(stream, Paths.get(location +
                    uploadedFileName), StandardCopyOption.REPLACE_EXISTING);
            return true;

        } catch (ServletException | IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    public Response getFile(Response response, String projectId, String fileType) throws IOException {
        String fileName;
        switch (fileType) {
            case "template": {
                fileName = projectsMap.get(projectId).getOutputSettings().getTemplateProperties().getTemplateName() + ".json";
                break;
            }
            case "index": {
                fileName = projectsMap.get(projectId).getOutputSettings().getIndexProperties().getIndexName() + ".json";
                break;
            }
            case "change_log": {
                fileName = EAppSettings.ANALYZER_CHANGELOG_FILE.getConfigurationParameter();
                break;
            }
            case "all": {
                fileName = EAppSettings.ANALYZER_ALL_LOGS_FILE.getConfigurationParameter();
                break;
            }
            default: {
                fileName = projectsMap.get(projectId).getOutputSettings().getTemplateProperties().getTemplateName() + ".json";
            }
        }
        if (fileName.equals("null.json")) {
            fileName = "empty_file.json";
        }
        File file = new File(TemplatesGenerator.projectsFolder + projectId + "/"
                + fileName);

        response.raw().setContentType("application/octet-stream");
        response.raw().setHeader("Content-Disposition", "attachment; filename=" + file.getName());

        response.raw().setContentLength((int) file.length());
        response.status(200);

        ServletOutputStream os = response.raw().getOutputStream();
        try {
            os.write(Files.readAllBytes(file.toPath()));
        } catch (NoSuchFileException e) {
            os.write(new byte[0]);
        }
        os.flush();
        os.close();
        return response;
    }

}
