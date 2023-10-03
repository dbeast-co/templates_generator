import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { LegacyTemplatesConverterRoutingModule } from './legacy-templates-converter-routing.module';
import { LegacyTemplatesConveterComponent } from './legacy-templates-conveter.component';
import { SourceConnectionComponent } from './components/source-connection/source-connection.component';
import {MaterialModule} from '../../shared/material.module';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {SharedModule} from '../../shared/shared.module';
import {NewProjectConfigurationModule} from '../new-project-configuration/new-project-configuration.module';
import { TemplatesTableComponent } from './components/templates-table/templates-table.component';
import { GenerateSettingsComponent } from './components/generate-settings/generate-settings.component';


@NgModule({
  declarations: [ LegacyTemplatesConveterComponent, SourceConnectionComponent, TemplatesTableComponent, GenerateSettingsComponent],
  imports: [
    CommonModule,
    LegacyTemplatesConverterRoutingModule,
    MaterialModule,
    ReactiveFormsModule,
    SharedModule,
    NewProjectConfigurationModule,
    FormsModule
  ]
})
export class LegacyTemplatesConverterModule { }
