import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {ProjectsMonitoringComponent} from './components/projects-monitoring/projects-monitoring.component';

const routes: Routes = [
  {
    path: '',
    redirectTo: '/projects_monitoring',
    pathMatch: 'full'
  },
  {
    path: 'projects_monitoring',
    component: ProjectsMonitoringComponent
  }

];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class CoreRoutingModule { }
