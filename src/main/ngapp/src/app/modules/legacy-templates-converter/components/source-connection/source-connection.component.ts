import { Component, OnInit } from '@angular/core';
import {MatCheckboxChange} from '@angular/material/checkbox';
import {FormControl, FormGroup} from '@angular/forms';
import {ProjectFormService} from '../../services/project-form.service';
import {ApiService} from '../../../../shared/api.service';
import {TestTemplatesStatus} from '../../../../models/test-templates-status';

@Component({
  selector: 'app-source-connection',
  templateUrl: './source-connection.component.html',
  styleUrls: ['./source-connection.component.scss']
})
export class SourceConnectionComponent implements OnInit {
  projectForm: FormGroup;
  showSourceSSLBtn: boolean = false;
  isDisableSourceTestButton: boolean = false;
  isTestSourceClicked: boolean = false;
  source_cluster_status: string = '';
  source_status_on_reload_page: string = '';
  es_source_host: FormControl;
  source_use_authentication_enabled: FormControl;
  source_username: FormControl;
  source_password: FormControl;
   projectId: FormControl;
  constructor(private projectFormService: ProjectFormService,private apiService: ApiService) { }

  ngOnInit(): void {
    this.projectId = this.projectFormService.project_id;
    this.projectForm = this.projectFormService.projectForm;
    this.es_source_host = this.projectFormService.es_source_host;
    this.source_use_authentication_enabled = this.projectFormService.source_use_authentication_enabled;
    this.source_username = this.projectFormService.source_username;
    this.source_password = this.projectFormService.source_password;
    this.source_cluster_status = this.projectFormService.source_cluster_status;
  }



  onUseSourceAuth($event: MatCheckboxChange) {

  }

  onSourceInput(value, value2) {

  }


  onSourceSSL($event: MatCheckboxChange) {

  }

  onImportSSL($event: Event, source: string) {

  }

  onTestSource() {
    const connection_settings = this.projectForm.get('connection_settings').value
    this.apiService.testTemplatesConverter(this.projectId.value,connection_settings).subscribe({
      next: (res: TestTemplatesStatus) => {
        this.isTestSourceClicked = true;
        this.source_cluster_status =  res.cluster_status;
        this.projectForm.get('connection_settings').get('status').patchValue(res.cluster_status);
      }
    })
  }

  onGetTemplates() {
    this.projectFormService.setTemplatesEvent(true);
  }

  onRadioButton(preventDefault: void, radio: any, i: number) {

  }

  onUseDestAuth($event: MatCheckboxChange) {

  }

  onDestinationInput(value, value2) {

  }

  onDestSSL($event: MatCheckboxChange) {

  }
}
