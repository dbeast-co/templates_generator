import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';

const routes: Routes = [

  {
    path: '',
    loadChildren: () => import('./modules/core/core.module').then((m) => m.CoreModule)
  },
  {
    path: 'project_configuration',
    loadChildren: () => import('./modules/new-project-configuration/new-project-configuration.module').then((m) => m.NewProjectConfigurationModule),
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {useHash: true})],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
