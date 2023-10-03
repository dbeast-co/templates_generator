import { Injectable } from '@angular/core';
import {
  FormArray,
  FormBuilder,
  FormControl,
  FormGroup,
  Validators,
} from '@angular/forms';
import { v4 as uuidv4 } from 'uuid';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ProjectFormService {
  projectForm: FormGroup;
  private getTemplatesEvent: BehaviorSubject<boolean> =
    new BehaviorSubject<boolean>(false);
  getTemplatesClicked$ = this.getTemplatesEvent.asObservable();

  constructor(private fb: FormBuilder) {
    this.initProjectForm();
  }

  get project_id() {
    return this.projectForm.get('project_id') as FormControl;
  }

  get es_source_host() {
    return this.projectForm
      .get('connection_settings')
      .get('es_host') as FormControl;
  }

  get source_use_authentication_enabled() {
    return this.projectForm
      .get('connection_settings')
      .get('authentication_enabled') as FormControl;
  }

  get source_username() {
    return this.projectForm
      .get('connection_settings')
      .get('username') as FormControl;
  }

  get source_password() {
    return this.projectForm
      .get('connection_settings')
      .get('password') as FormControl;
  }

  get source_cluster_status() {
    return this.projectForm.get('connection_settings').get('status')
      .value as string;
  }

  get legacy_templates_list() {
    return this.projectForm.get('legacy_templates_list') as FormArray;
  }

  get source_index_list() {
    return this.projectForm.get('source_index_list') as FormArray;
  }

  setTemplatesEvent(clicked: boolean): void {
    this.getTemplatesEvent.next(clicked);
  }

  initProjectForm() {
    const hostRegex = '(http|https):\\/\\/((\\w|-|\\d|_|\\.)+)\\:\\d{2,5}';

    this.projectForm = this.fb.group({
      project_id: [uuidv4()],
      connection_settings: this.fb.group({
        es_host: [
          'https://44.195.2.0:9200',
          [Validators.required, Validators.pattern(hostRegex)],
        ],
        authentication_enabled: [true],
        username: ['monitoring', Validators.required],
        password: ['qwe123', Validators.required],
        ssl_enabled: [true],
        ssl_file: [null],
        status: ['YELLOW'],
      }),
      is_generate_dedicated_components_template: [true],
      is_separate_mappings_and_settings: [true],
      legacy_templates_list: this.fb.array([]),
    });
  }
}
