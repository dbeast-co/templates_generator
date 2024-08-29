import {APP_INITIALIZER, ModuleWithProviders, NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {MaterialModule} from './material.module';
import {ApiService} from './api.service';
import {ToastrModule} from 'ngx-toastr';
import {YesNoComponent} from './yes-no/yes-no.component';
import {DownloadService} from './download.service';
import {AppConfigService} from './app-config.service';
import {AppConfig} from '../models/app.config';
import {HttpClient} from '@angular/common/http';
import {ErrorComponent} from './error/error.component';
import {DisableControlDirective} from './disable-control.directive';
import {ReactiveFormsModule} from '@angular/forms';
import {DisableEnterDirective} from './directives/disable-enter.directive';

export function initializeAppFn(
  appConfigService: AppConfigService
): () => Promise<void> {
  return () => {
    return appConfigService.getAppConfig();
  };
}

@NgModule({
  declarations: [YesNoComponent, ErrorComponent, DisableControlDirective,DisableEnterDirective],
  exports: [YesNoComponent, ErrorComponent, DisableControlDirective,DisableEnterDirective],
  imports: [
    CommonModule,
    MaterialModule,
    ToastrModule.forRoot({
      positionClass: 'toast-bottom-right',
    }),

  ],
})
export class SharedModule {
  static forRoot(): ModuleWithProviders<any> {
    return {
      ngModule: SharedModule,
      providers: [
        ApiService,
        DownloadService,
        AppConfigService,
        {
          provide: AppConfig,
          deps: [HttpClient],
          useExisting: AppConfigService,
        },
        {
          provide: APP_INITIALIZER,
          multi: true,
          deps: [AppConfigService],
          useFactory: initializeAppFn,
        },
      ],
    };
  }
}
