import { APP_INITIALIZER, LOCALE_ID, NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { CoreRoutingModule } from './core-routing.module';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { HeaderComponent } from './components/header/header.component';
import { ProjectsMonitoringComponent } from './components/projects-monitoring/projects-monitoring.component';
import { SharedModule } from '../../shared/shared.module';
import { MaterialModule } from '../../shared/material.module';
import { CoreStore } from './services/core-store';
import { YesNoComponent } from '../../shared/yes-no/yes-no.component';
import { AppConfig } from '../../models/app.config';
import { ReactiveFormsModule } from '@angular/forms';

@NgModule({
  declarations: [HeaderComponent, ProjectsMonitoringComponent],
  exports: [HeaderComponent],
  imports: [
    CommonModule,
    CoreRoutingModule,
    HttpClientModule,
    SharedModule.forRoot(),
    MaterialModule,
    ReactiveFormsModule,
  ],
  providers: [
    CoreStore,
    { provide: LOCALE_ID, useValue: 'en-US' },
    { provide: Window, useValue: window },
  ],
})
export class CoreModule {}
