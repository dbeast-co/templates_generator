import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {LegacyTemplatesConveterComponent} from './legacy-templates-conveter.component';

const routes: Routes = [
  {
    path: '',
    component: LegacyTemplatesConveterComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class LegacyTemplatesConverterRoutingModule { }
