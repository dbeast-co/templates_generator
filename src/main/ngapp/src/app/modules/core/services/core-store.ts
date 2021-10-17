import {BehaviorSubject, Observable} from 'rxjs';
import {ProjectConfigurationModel} from '../../../models/project-configuration.model';
import {ApiService} from '../../../shared/api.service';
import {Injectable} from '@angular/core';

@Injectable()
export class CoreStore {
  // private _newProjectState: BehaviorSubject<ProjectConfigurationModel> = new BehaviorSubject<ProjectConfigurationModel>(null);
  // private _savedConfigState: BehaviorSubject<ProjectConfigurationModel> = new BehaviorSubject<ProjectConfigurationModel>(null);
  //
  // constructor(private api: ApiService) {
  //   this.getNewProject();
  //
  // }
  //
  // getNewConfigState(): Observable<ProjectConfigurationModel> {
  //     return this._newProjectState.asObservable();
  // }
  //
  //
  // private getNewProject(): void {
  //   this.api.getNewProjectConfiguration().subscribe((res) => {
  //     this._newProjectState.next(res);
  //   });
  // }
  //
  // getSavedProject(project_id: string){
  //
  // }
}
