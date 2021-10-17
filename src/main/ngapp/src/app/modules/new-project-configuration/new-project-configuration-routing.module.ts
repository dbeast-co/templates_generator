import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {ProjectConfigurationComponent} from './components/project-configuration/project-configuration.component';

const routes: Routes = [
  {
    path: '',
    component: ProjectConfigurationComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class NewProjectConfigurationRoutingModule { }
