import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { NewProjectConfigurationRoutingModule } from './new-project-configuration-routing.module';
import { SharedModule } from '../../shared/shared.module';
import { MaterialModule } from '../../shared/material.module';
import { ProjectConfigurationComponent } from './components/project-configuration/project-configuration.component';
import { FormBuilder, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { FormService } from './services/form.service';
import { DisableEnterDirective } from './directives/disable-enter.directive';
import { CustomSpinnerDirective } from './directives/custom-spinner.directive';

@NgModule({
  declarations: [
    ProjectConfigurationComponent,
    DisableEnterDirective,
    CustomSpinnerDirective,
  ],
  imports: [
    CommonModule,
    NewProjectConfigurationRoutingModule,
    SharedModule.forRoot(),
    MaterialModule,
    ReactiveFormsModule,
    FormsModule,
  ],
  exports: [],
  providers: [FormBuilder, FormService],
})
export class NewProjectConfigurationModule {}
