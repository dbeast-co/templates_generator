import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import {
  IClusterStatus,
  ProjectConfigurationModel,
} from '../models/project-configuration.model';
import { ISavedProject } from '../models/i-saved-project';
import { IProjectMonitoring } from '../models/project-monitoring';
import { AnalyzeResponseModel } from '../models/analyze-response.model';
import { FormGroup } from '@angular/forms';
import { LegacyTemplate } from '../models/legacy-template';

@Injectable({
  providedIn: 'root',
})
export class ApiService {
  constructor(private http: HttpClient) {}

  getNewProjectConfiguration(): Observable<ProjectConfigurationModel> {
    return this.http.get<ProjectConfigurationModel>(
      `${environment.serverApi}/index_analyzer/project_settings/new`
    );
  }

  getSavedProjectConfiguration(
    id: string
  ): Observable<ProjectConfigurationModel> {
    return this.http.get<ProjectConfigurationModel>(
      `${environment.serverApi}/index_analyzer/project_settings/get/${id}`
    );
  }

  getTestCluster(
    clusterSettings,
    project_id: string
  ): Observable<IClusterStatus> {
    return this.http.post<IClusterStatus>(
      `${environment.serverApi}/index_analyzer/project_settings/test_cluster/${project_id}`,
      clusterSettings
    );
  }

  getSavedProjects(): Observable<Array<ISavedProject>> {
    return this.http.get<Array<ISavedProject>>(
      `${environment.serverApi}/index_analyzer/project_settings/list`
    );
  }

  saveProject(project): Observable<boolean> {
    return this.http.post<boolean>(
      `${environment.serverApi}/index_analyzer/project_settings/save`,
      project
    );
  }

  analyzeProject(project_id): Observable<AnalyzeResponseModel> {
    return this.http.get<AnalyzeResponseModel>(
      `${environment.serverApi}/index_analyzer/project_settings/start/${project_id}`
    );
  }

  stopProject(project_id): Observable<boolean> {
    return this.http.get<boolean>(
      `${environment.serverApi}/index_analyzer/project_settings/stop/${project_id}`
    );
  }

  downloadTemplate(project_id): Observable<HttpResponse<Blob>> {
    return this.http.get(
      `${environment.serverApi}/index_analyzer/project_settings/download_template/${project_id}`,
      {
        responseType: 'blob',
        observe: 'response',
      }
    );
  }

  downloadIndex(project_id): Observable<HttpResponse<Blob>> {
    return this.http.get(
      `${environment.serverApi}/index_analyzer/project_settings/download_index/${project_id}`,
      {
        responseType: 'blob',
        observe: 'response',
      }
    );
  }

  downloadLogs(project_id): Observable<HttpResponse<Blob>> {
    return this.http.get(
      `${environment.serverApi}/index_analyzer/project_settings/download_change_log/${project_id}`,
      {
        responseType: 'blob',
        observe: 'response',
      }
    );
  }

  downloadAll(project_id): Observable<HttpResponse<Blob>> {
    return this.http.get(
      `${environment.serverApi}/index_analyzer/project_settings/download_all/${project_id}`,
      {
        responseType: 'blob',
        observe: 'response',
      }
    );
  }


  validateProjectName(project_name): Observable<boolean> {
    return this.http.get<boolean>(
      `${environment.serverApi}/index_analyzer/project_settings/validate/${project_name}`
    );
  }

  deleteProject(project_id: string): Observable<boolean> {
    return this.http.delete<boolean>(
      `${environment.serverApi}/index_analyzer/project_settings/delete/${project_id}`
    );
  }

  getSavedProjectsForMonitoring(): Observable<Array<IProjectMonitoring>> {
    return this.http.get<Array<IProjectMonitoring>>(
      `${environment.serverApi}/index_analyzer/projects_monitoring/projects_status`
    );
  }

  sendSSL(
    formData: FormData,
    project_id: string,
    source: string
  ): Observable<any> {
    return this.http.post<any>(
      `${environment.serverApi}/index_analyzer/project_settings/ssl_cert/${source}/${project_id}`,
      formData
    );
  }

  testTemplatesConverter(project_id: string, form: any): Observable<Object> {
    return this.http.post(
      `${environment.serverApi}/templates_converter/test_cluster/${project_id}`,
      form
    );
  }

  getLegacyTemplates(form: FormGroup): Observable<LegacyTemplate> {
    return this.http.post<LegacyTemplate>(
      `${environment.serverApi}/templates_converter/get_templates`,
      form.value
    );
  }

  runTemplatesConverter(form: FormGroup): Observable<any> {
    return this.http.post(
      `${environment.serverApi}/templates_converter/run`,
      form.value
    );
  }

  downloadIndexTemplate(project_id): Observable<HttpResponse<Blob>> {
    return this.http.get(
      `${environment.serverApi}/templates_converter/download/${project_id}`,
      {
        responseType: 'blob',
        observe: 'response',
      }
    );
  }
}
