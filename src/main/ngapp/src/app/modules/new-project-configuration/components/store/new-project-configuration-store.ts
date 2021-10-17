import {BehaviorSubject, Observable} from 'rxjs';
import {ProjectConfigurationModel} from '../../../../models/project-configuration.model';
import {ApiService} from '../../../../shared/api.service';
import {Injectable} from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class NewProjectConfigurationStore {
  private _projectState: BehaviorSubject<ProjectConfigurationModel> = new BehaviorSubject<ProjectConfigurationModel>(null);


  constructor(private api: ApiService) {
    // this.getNewProject();

  }

  getNewConfigState(): Observable<ProjectConfigurationModel> {
    this.getNewProject();
    return this._projectState.asObservable();
  }

  getSavedConfigState(id: string): Observable<ProjectConfigurationModel> {
    this.getSavedProject(id);
    return this._projectState.asObservable();
  }


  private getNewProject(): void {
    this.api.getNewProjectConfiguration().subscribe((res) => {
      this._projectState.next(res);
    });
  }

  private getSavedProject(id: string): void {
    this.api.getSavedProjectConfiguration(id).subscribe((res) => {
      this._projectState.next(res);
    });
  }


}
