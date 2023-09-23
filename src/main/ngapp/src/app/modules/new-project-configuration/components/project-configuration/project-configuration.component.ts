import {
  AfterViewInit,
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  ElementRef,
  Inject,
  OnDestroy,
  OnInit,
  Renderer2,
  ViewChild,
} from '@angular/core';
import {Observable, ReplaySubject} from 'rxjs';
import {ProjectConfigurationModel} from '../../../../models/project-configuration.model';
import {NewProjectConfigurationStore} from '../store/new-project-configuration-store';
import {FormService} from '../../services/form.service';
import {AbstractControl, FormControl, FormGroup, FormGroupName, Validators} from '@angular/forms';
import {MatCheckboxChange} from '@angular/material/checkbox';
import {SubSink} from 'subsink';
import {ApiService} from '../../../../shared/api.service';
import {ToastrService} from 'ngx-toastr';
import {ActivatedRoute, Router} from '@angular/router';
import {DownloadService} from '../../../../shared/download.service';
import {HttpErrorResponse} from '@angular/common/http';
import {takeUntil} from 'rxjs/operators';
import {DOCUMENT} from '@angular/common';
import {HeaderService} from '../../../../shared/header.service';

@Component({
  selector: 'yl-project-configuration',
  templateUrl: './project-configuration.component.html',
  styleUrls: ['./project-configuration.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProjectConfigurationComponent implements OnInit, OnDestroy,AfterViewInit {
  newProject: Observable<ProjectConfigurationModel>;
  newProjectForm: FormGroup;
  isDisableSourceTestButton: boolean = false;
  showSourceSSLBtn: boolean = false;
  private subs = new SubSink();
  isShowErrorDialog: boolean = false;
  isOnTest: boolean = false;
  isTestSourceClicked: boolean = false;
  @ViewChild('fileSourceRef') fileSourceRef: ElementRef;
  @ViewChild('template_properties_containerRef')
  template_properties_containerRef: ElementRef;
  isUploadSSLFileForSource: boolean = false;
  isSuccessForSource: boolean = false;
  isErrorForSource: boolean = false;
  isActiveOverlay: boolean = false;
  showSpinner: boolean = false;
  startStopBtn: string = 'Analyze';
  playIcon: string = 'play_arrow-white-18dp.svg';
  timer: number;
  mapping_changes_logString: string = '';
  showValidationNameErrorRequired: boolean = false;
  showValidationNameError: boolean = false;
  isShowYesNoDialog: boolean = false;
  project: ProjectConfigurationModel;
  errorMessage: string = '';
  isDisableButton: boolean;
  private destroyed$: ReplaySubject<boolean> = new ReplaySubject(1);
@ViewChild('addAllFieldsForIndexProperties')addAllFieldsForIndexProperties: MatCheckboxChange;
  constructor(
    private store: NewProjectConfigurationStore,
    private formService: FormService,
    private cdr: ChangeDetectorRef,
    private apiService: ApiService,
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private downloadService: DownloadService,
    private toastr: ToastrService,
    private headerService: HeaderService,
    private renderer: Renderer2,
    @Inject(DOCUMENT) private document: Document
  ) {
  }
  ngAfterViewInit(): void {
    console.log(this.addAllFieldsForIndexProperties)
    if(this.addAllFieldsForIndexProperties.checked){
      console.log('checked');
    }else{
      console.log('not checked');
    }
  }

  ngOnInit(): void {
    this.activatedRoute.queryParams.subscribe((params) => {
      if (params.id === undefined || params.id === '') {
        this.store
          .getNewConfigState()
          .pipe(takeUntil(this.destroyed$))
          .subscribe((data) => {
            this.project = data;
            this.checkIsInActiveProcess(this.project);
            this.newProjectForm = this.formService.getProjectConfigurationForm(
              this.project
            );
            this.headerService.setHeaderTitle('Project settings');
            this.newProjectForm.valueChanges.subscribe((control) => {
              this.isDisableButton = this.newProjectForm.invalid;
            });
            this.isActiveOverlay = false;
            this.showSpinner = false;
            this.startStopBtn = 'Analyze';
            this.playIcon = 'play_arrow-white-18dp.svg';
            this.setValidatorsByCheckboxes();
            this.checkGenerateTemplateAndGenerateIndex();

            this.cdr.detectChanges();
          });
      } else {
        this.store
          .getSavedConfigState(params.id)
          .pipe(takeUntil(this.destroyed$))
          .subscribe((data) => {
            this.newProjectForm =
              this.formService.getProjectConfigurationForm(data);
            this.headerService.setHeaderTitle('Project settings');
            this.newProjectForm.valueChanges.subscribe((control) => {
              if (
                this.project_name.errors ||
                this.template_nameForTemplate.errors ||
                this.index_for_analyze.errors ||
                this.index_name.errors ||
                this.is_generate_template.errors ||
                this.is_generate_index.errors
              ) {
                this.isDisableButton = true;
              } else {
                this.isDisableButton = false;
              }
            });
            this.project = data;
            this.checkGenerateTemplateAndGenerateIndex();
            this.checkIsInActiveProcess(this.project);
            const event: MatCheckboxChange = {
              checked: false,
              source: null,
            };
            this.onUseSourceAuth(event);
            this.formatMappingChangesLog();
            this.cdr.markForCheck();
          });
      }
    });
  }

  setValidatorsByCheckboxes(): void {
    if (this.is_generate_template.value === false) {
      // this.disableControls(this.template_properties);
      this.clearValidators(this.template_nameForTemplate);
      this.clearValidators(this.index_patternsForTemplate);
      this.cdr.markForCheck();
    } else {
      // this.enableControls(this.template_properties);
      this.setValidators(this.template_nameForTemplate);
      this.setValidators(this.index_patternsForTemplate);
      this.cdr.markForCheck();
    }

    if (this.is_generate_index.value === false) {
      this.clearValidators(this.template_nameForIndex);
      this.cdr.markForCheck();
    } else {
      this.setValidators(this.template_nameForIndex);
      this.cdr.markForCheck();
    }

    if (this.source_use_authentication_enabled.value === false) {
      this.source_username.disable();
      this.source_password.disable();
      this.source_username.clearValidators();
      this.source_password.clearValidators();
      this.cdr.markForCheck();
    } else {
      this.source_username.enable();
      this.source_password.enable();
      this.setValidators(this.source_username);
      this.setValidators(this.source_password);
      this.cdr.markForCheck();
    }
  }

  checkGenerateTemplateAndGenerateIndex(): void {
    if (
      this.newProjectForm.get('actions').get('is_generate_template').value ===
      false
    ) {
      this.disableControls(this.template_properties);
      this.disableControls(this.existing_template_actions);
    }else {
      this.enableControls(this.template_properties);
      this.enableControls(this.existing_template_actions);
    }

    if (
      this.newProjectForm.get('actions').get('is_generate_index').value ===
      false
    ) {
      this.disableControls(this.index_properties);
      this.disableControls(this.existing_template_actionsForIndex);
    }else {
      this.enableControls(this.index_properties);
      this.enableControls(this.existing_template_actionsForIndex);
    }
  }

  checkIsInActiveProcess(project: ProjectConfigurationModel): void {
    if (this.project?.status.is_in_active_process) {
      this.onAnalyze('Analyze');
    } else {
    }
  }

  formatMappingChangesLog(): void {
    this.mapping_changes_logString = JSON.stringify(
      this.newProjectForm.get('mapping_changes_log').value
    );
    // tslint:disable-next-line:max-line-length
    const mapping_changes_logStringFormatted = this.mapping_changes_logString
      .replace(/,/g, '\n')
      .replace(/"/g, '')
      .replace('[', '')
      .replace(']', '');
    this.newProjectForm
      .get('mapping_changes_log')
      .patchValue(mapping_changes_logStringFormatted);
    this.cdr.markForCheck();
  }

  //region Getters
  get project_id(): any {
    return this.newProjectForm?.get('project_id').value;
  }

  get input(): AbstractControl {
    this.cdr.markForCheck();
    return this.newProjectForm?.get('input');
  }

  get source_cluster(): AbstractControl {
    this.cdr.markForCheck();
    return this.newProjectForm?.get('input').get('source_cluster');
  }

  get es_host(): AbstractControl {
    this.cdr.markForCheck();
    return this.newProjectForm
      ?.get('input')
      .get('source_cluster')
      .get('es_host');
  }

  get source_use_authentication_enabled(): AbstractControl | undefined {
    return this.newProjectForm
      ?.get('input')
      .get('source_cluster')
      .get('authentication_enabled');
  }

  get source_username(): AbstractControl | undefined {
    this.cdr.markForCheck();
    return this.newProjectForm
      ?.get('input')
      .get('source_cluster')
      .get('username');
  }

  get source_password(): AbstractControl | undefined {
    this.cdr.markForCheck();
    return this.newProjectForm
      ?.get('input')
      .get('source_cluster')
      .get('password');
  }

  get ssl_enabled(): AbstractControl | undefined {
    this.cdr.markForCheck();

    return this.newProjectForm
      ?.get('input')
      .get('source_cluster')
      .get('ssl_enabled');
  }

  get source_cluster_status(): AbstractControl | undefined {
    this.cdr.markForCheck();

    return this.newProjectForm
      ?.get('input')
      .get('source_cluster')
      .get('status');
  }

  //endregion

  //region Getters for Output
  get output(): AbstractControl {
    this.cdr.markForCheck();
    return this.newProjectForm?.get('output');
  }

  get index_properties(): FormGroup {
    this.cdr.markForCheck();
    return this.newProjectForm
      ?.get('output')
      .get('index_properties') as FormGroup;
  }

  get index_name(): AbstractControl | undefined {
    this.cdr.markForCheck();
    return this.newProjectForm
      ?.get('output')
      .get('index_properties')
      .get('index_name');
  }

  //endregion

  //region Getters for Template properties
  get template_properties(): FormGroup | undefined {
    return this.newProjectForm
      ?.get('output')
      ?.get('template_properties') as FormGroup;
  }

  get existing_template_actions(): FormGroup {
    return this.newProjectForm
      ?.get('output')
      ?.get('template_properties')
      ?.get('existing_template_actions') as FormGroup;
  }

  get is_add_fields_from_existing_templateForTemplate():
    | AbstractControl
    | undefined {
    return this.newProjectForm
      ?.get('output')
      ?.get('template_properties')
      ?.get('existing_template_actions')
      ?.get('is_add_fields_from_existing_templateForTemplate');
  }

  get is_add_fields_from_at_least_one_time_used_root_level():
    | AbstractControl
    | undefined {
    return this.newProjectForm
      ?.get('output')
      ?.get('template_properties')
      ?.get('existing_template_actions')
      ?.get('is_add_fields_from_at_least_one_time_used_root_level');
  }

  get is_replace_existing_field_types(): AbstractControl | undefined {
    return this.newProjectForm
      ?.get('output')
      ?.get('template_properties')
      ?.get('existing_template_actions')
      ?.get('is_replace_existing_field_types');
  }

  get is_add_settings(): AbstractControl | undefined {
    return this.newProjectForm
      ?.get('output')
      ?.get('template_properties')
      ?.get('existing_template_actions')
      ?.get('is_add_settings');
  }

  get is_add_dynamic_mapping(): AbstractControl | undefined {
    return this.newProjectForm
      ?.get('output')
      ?.get('template_properties')
      ?.get('existing_template_actions')
      ?.get('is_add_dynamic_mapping');
  }

  get is_generate_dedicated_none_existing_fields_template():
    | AbstractControl
    | undefined {
    return this.newProjectForm
      ?.get('output')
      ?.get('template_properties')
      ?.get('existing_template_actions')
      ?.get('is_generate_dedicated_none_existing_fields_template');
  }

  get is_ignore_type_conflicts(): AbstractControl | undefined {
    return this.newProjectForm
      ?.get('output')
      ?.get('template_properties')
      ?.get('existing_template_actions')
      ?.get('is_ignore_type_conflicts');
  }

  get add_fields_from_existing_template_name(): AbstractControl | undefined {
    return this.newProjectForm
      ?.get('output')
      ?.get('template_properties')
      ?.get('existing_template_actions')
      ?.get('add_fields_from_existing_template_name');
  }

  get add_fields_from_existing_template_name_index(): AbstractControl | undefined {
    return this.newProjectForm
      ?.get('output')
      ?.get('index_properties')
      ?.get('existing_template_actions')
      ?.get('add_fields_from_existing_template_name');
  }

  get is_add_all_fields(): AbstractControl | undefined {
    return this.newProjectForm
      ?.get('output')
      ?.get('template_properties')
      ?.get('existing_template_actions')
      ?.get('is_add_all_fields');
  }

  get template_nameForTemplate(): AbstractControl | undefined {
    return this.newProjectForm
      ?.get('output')
      ?.get('template_properties')
      ?.get('template_name');
  }

  get order(): AbstractControl | undefined {
    return this.newProjectForm
      ?.get('output')
      ?.get('template_properties')
      .get('order');
  }

  get index_patternsForTemplate(): AbstractControl | undefined {
    return this.newProjectForm
      ?.get('output')
      ?.get('template_properties')
      ?.get('index_patterns');
  }

  get number_of_shards(): AbstractControl | undefined {
    return this.newProjectForm
      ?.get('output')
      ?.get('template_properties')
      ?.get('index_settings')
      ?.get('number_of_shards');
  }

  get codec(): AbstractControl | undefined {
    return this.newProjectForm
      ?.get('output')
      ?.get('template_properties')
      ?.get('index_settings')
      ?.get('codec');
  }

  get number_of_replicas(): AbstractControl | undefined {
    return this.newProjectForm
      ?.get('output')
      ?.get('template_properties')
      ?.get('index_settings')
      ?.get('number_of_replicas');
  }

  get refresh_interval(): AbstractControl | undefined {
    return this.newProjectForm
      ?.get('output')
      ?.get('template_properties')
      ?.get('index_settings')
      ?.get('refresh_interval');
  }
  get isGenerateDedicatedComponentsTemplate():FormControl | undefined {
    return this.newProjectForm
      ?.get('actions')
      ?.get('is_generate_dedicated_components_template') as FormControl;
  }
  get isSeparateMappingsAndSettings(): FormControl | undefined {
    return this.newProjectForm
      ?.get('actions')
      ?.get('is_separate_mappings_and_settings') as FormControl;
  }

  //endregion

  //region Getters for Input settings
  get input_settings(): AbstractControl {
    return this.newProjectForm.get('input').get('input_settings');
  }

  get index_for_analyze(): AbstractControl {
    return this.newProjectForm
      .get('input')
      .get('input_settings')
      .get('index_for_analyze');
  }

  get max_docs_for_analyze(): AbstractControl {
    return this.newProjectForm
      .get('input')
      .get('input_settings')
      .get('max_docs_for_analyze');
  }

  get scroll_size(): AbstractControl {
    return this.newProjectForm
      .get('input')
      .get('input_settings')
      .get('scroll_size');
  }

  get template_nameForIndex(): AbstractControl | undefined {
    return this.newProjectForm
      ?.get('output')
      ?.get('index_properties')
      ?.get('index_name');
  }

  get existing_template_actionsForIndex(): FormGroup {
    return this.newProjectForm
      ?.get('output')
      ?.get('index_properties')
      ?.get('existing_template_actions') as FormGroup;
  }

  get is_add_fields_from_existing_templateForIndex():
    | AbstractControl
    | undefined {
    return this.newProjectForm
      ?.get('output')
      ?.get('index_properties')
      ?.get('existing_template_actions')
      ?.get('is_add_fields_from_existing_templateForTemplate');
  }

  get is_add_all_fieldsForIndex(): AbstractControl | undefined {
    return this.newProjectForm
      ?.get('output')
      ?.get('index_properties')
      ?.get('existing_template_actions')
      ?.get('is_add_all_fields');
  }

  get is_add_fields_from_at_least_one_time_used_root_levelForIndex():
    | AbstractControl
    | undefined {
    return this.newProjectForm
      ?.get('output')
      ?.get('index_properties')
      ?.get('existing_template_actions')
      ?.get('is_add_fields_from_at_least_one_time_used_root_level');
  }

  // --------------------------------------------------------------

  // ----------------------------- Actions ------------------------
  get actions(): AbstractControl {
    return this.newProjectForm.get('actions');
  }

  get is_generate_index(): AbstractControl {
    return this.newProjectForm.get('actions').get('is_generate_index');
  }

  get is_generate_template(): AbstractControl {
    return this.newProjectForm.get('actions').get('is_generate_template');
  }

  //endregion

  //region Getters for mapping change log
  get mapping_changes_log(): AbstractControl {
    return this.newProjectForm.get('mapping_changes_log');
  }

  //endregion

  //region Getters for status
  get status(): AbstractControl {
    return this.newProjectForm.get('status');
  }

  get analyzed_docs(): AbstractControl {
    return this.newProjectForm.get('status').get('analyzed_docs');
  }

  get docs_for_analyze(): AbstractControl {
    return this.newProjectForm.get('status').get('docs_for_analyze');
  }

  get docs_in_index(): AbstractControl {
    return this.newProjectForm.get('status').get('docs_in_index');
  }

  get execution_progress(): AbstractControl {
    return this.newProjectForm.get('status').get('execution_progress');
  }

  get is_done(): AbstractControl {
    return this.newProjectForm.get('status').get('is_done');
  }

  get is_failed(): AbstractControl {
    return this.newProjectForm.get('status').get('is_failed');
  }

  get is_in_active_process(): AbstractControl {
    return this.newProjectForm.get('status').get('is_in_active_process');
  }

  get project_status(): AbstractControl {
    return this.newProjectForm.get('status').get('project_status');
  }

  get project_name(): AbstractControl {
    return this.newProjectForm.get('project_name');
  }

  //endregion

  onInputProjectName(project_name): void {
    this.showValidationNameErrorRequired = false;
    this.apiService.validateProjectName(project_name).subscribe(
      (result: boolean) => {
        this.showValidationNameError = result;
        this.cdr.markForCheck();
      },
      (err) => {
        this.toastr.error(`Server not responding! Please try again later`, '', {
          positionClass: 'toast-top-right',
        });
      }
    );
    if (this.newProjectForm.get('project_name').value === '') {
      this.showValidationNameErrorRequired = true;
      this.isDisableButton = true;
      this.cdr.markForCheck();
    }
  }

  onUseSourceAuth(event: MatCheckboxChange): void {
    if (event.checked) {
      this.setValidators(this.source_username);
      this.setValidators(this.source_password);
      this.source_username.enable();
      this.source_password.enable();
      this.isDisableSourceTestButton = true;
      this.cdr.markForCheck();
      this.isDisableSourceTestButton = !(
        this.source_password.value !== null &&
        this.source_username.value !== null
      );
    } else {
      this.clearValidators(this.source_username);
      this.clearValidators(this.source_password);
      this.source_username.disable();
      this.source_password.disable();
      this.source_username.reset();
      this.source_password.reset();
      this.cdr.markForCheck();
      this.isDisableSourceTestButton = false;
    }
  }

  onSourceInput(username: any, password: any = ''): void {
    this.isDisableSourceTestButton = !(username && password);
  }

  onSourceSSL(event: MatCheckboxChange): void {
    this.showSourceSSLBtn = event.checked;
  }

  onTestSource(projectForm: FormGroup, target: string): void {
    this.onTestSourceOrDestination(projectForm, target);
  }

  onTestSourceOrDestination(projectForm: FormGroup, target: string): void {
    this.cdr.markForCheck();
    const clusterSettings = {
      es_host: this.es_host.value,
      authentication_enabled: this.source_use_authentication_enabled.value,
      username: this.source_username.value,
      password: this.source_password.value,
      ssl_enabled: this.ssl_enabled.value,
      ssl_file: this.newProjectForm
        ?.get('connection_settings')
        ?.get(`${target}`)
        ?.get('ssl_file').value,
      status: this.source_cluster_status.value,
    };
    this.subs.add(
      this.apiService
        .getTestCluster(
          clusterSettings,
          this.newProjectForm.get('project_id').value
        )
        .subscribe(
          (result) => {
            if (result) {
              this.isOnTest = false;
              if (target === 'source') {
                this.source_cluster_status.patchValue(result.cluster_status);

                this.cdr.markForCheck();
              }
            }
            this.isTestSourceClicked = true;
          },
          (error: HttpErrorResponse) => {
            this.source_cluster_status.patchValue(error.error.cluster_status);
            this.isShowErrorDialog = true;
            this.errorMessage = error.error.error;
          }
        )
    );
  }

  onImportSSL(event, target: string): void {
    this.cdr.markForCheck();
    const formData = new FormData();
    if (event.target.files.length > 0) {
      const pattern = new RegExp('([a-zA-Z0-9\\s_\\\\.\\-:])+(.*)$');
      if (pattern.test(event.target.files[0].name)) {
        formData.append('file', event.target.files[0]);

        this.isUploadSSLFileForSource = true;

        this.apiService
          .sendSSL(
            formData,
            this.newProjectForm.get('project_id').value,
            target
          )
          .subscribe(
            (res) => {
              if (res) {
                this.isUploadSSLFileForSource = false;
                this.isSuccessForSource = true;
                this.fileSourceRef.nativeElement.value = '';
                this.cdr.markForCheck();

                this.toastr.success('File was successfully uploaded!', '');
              } else {
                this.isSuccessForSource = false;
                this.isUploadSSLFileForSource = false;
                this.isErrorForSource = true;
                this.cdr.markForCheck();
                this.toastr.error('Something went wrong!', '');
              }
            },
            (err) => {
              // if (target === 'source') {
              this.isSuccessForSource = false;
              this.isUploadSSLFileForSource = false;
              this.isErrorForSource = true;
            }
          );
      }

      this.newProjectForm
        ?.get('connection_settings')
        ?.get('source')
        ?.get('ssl_file')
        .patchValue(target + '_' + event.target.files[0].name);
    }
  }

  getProjectStatus(): string {
    if (
      this.project_status.value === 'NEW' ||
      this.project_status.value === 'STOPPED'
    ) {
      return 'new_stopped';
    }
    if (this.project_status.value === 'FAILED') {
      return 'danger';
    }
    if (this.project_status.value === 'SUCCEEDED') {
      return 'success';
    }
    if (this.project_status.value === 'ON_FLY') {
      return 'warn';
    }
  }

  onSaveProject(): void {
    this.mapping_changes_log.patchValue([]);
    this.apiService.saveProject(this.newProjectForm.value).subscribe(
      (res) => {
        if (res) {
          this.toastr.success('Project was successfully saved!', '');
        } else {
          this.toastr.error('Something went wrong!', '');
        }
      },
      () => {
      }
    );
  }

  onAnalyze(startStopBtn: string): void {
    if (startStopBtn === 'Analyze') {
      this.isActiveOverlay = true;
      this.showSpinner = true;
      this.startStopBtn = 'Stop';
      this.playIcon = 'stop_black_24dp.svg';
      this.cdr.markForCheck();
      this.startProgressbar();
      this.apiService
        .analyzeProject(this.newProjectForm?.get('project_id').value)
        .subscribe(
          (res) => {
            if (res) {
              this.startStopBtn = 'Analyze';
              this.playIcon = 'play_arrow-white-18dp.svg';
              this.showSpinner = false;
              this.isActiveOverlay = false;
              this.cdr.markForCheck();
            }
          },
          (err: HttpErrorResponse) => {
            this.isShowErrorDialog = true;
            this.errorMessage = err.error;
            this.cdr.markForCheck();
          }
        );
    }
    if (startStopBtn === 'Stop') {
      this.cdr.markForCheck();
      this.apiService
        .stopProject(this.newProjectForm.get('project_id').value)
        .subscribe((res) => {
          this.isActiveOverlay = false;
          this.showSpinner = false;
          this.startStopBtn = 'Analyze';
          this.playIcon = 'play_arrow-white-18dp.svg';
          clearInterval(this.timer);
          this.cdr.markForCheck();
        });
    }
  }

  startProgressbar(): void {
    this.cdr.markForCheck();
    this.timer = window.setInterval(() => {
      this.apiService
        .getSavedProjectConfiguration(
          this.newProjectForm.get('project_id').value
        )
        .subscribe(
          (data) => {
            this.execution_progress.patchValue(data.status.execution_progress);
            this.project_status.patchValue(data.status.project_status);
            this.is_done.patchValue(data.status.is_done);
            this.mapping_changes_log.patchValue(data.mapping_changes_log);
            this.docs_in_index.patchValue(data.status.docs_in_index);
            this.docs_for_analyze.patchValue(data.status.docs_for_analyze);
            this.analyzed_docs.patchValue(data.status.analyzed_docs);
            this.formatMappingChangesLog();
            this.getProjectStatus();
            this.cdr.markForCheck();
            if (this.is_done.value) {
              this.startStopBtn = 'Analyze';
              this.playIcon = 'play_arrow-white-18dp.svg';
              this.isActiveOverlay = false;
              this.showSpinner = false;
              clearInterval(this.timer);
              this.cdr.markForCheck();
            }
          },
          (err) => {
            this.toastr.error(
              `Server not responding! Please try again later`,
              '',
              {
                positionClass: 'toast-top-right',
              }
            );
          }
        );
    }, 1000);
  }

  onDownloadTemplate(): void {
    this.downloadService.onDownloadTemplate(
      this.newProjectForm.get('project_id').value
    );
  }

  onDownloadIndex(): void {
    this.downloadService.onDownloadIndex(
      this.newProjectForm.get('project_id').value
    );
  }

  onDownloadLogs(): void {
    this.downloadService.onDownloadLogs(
      this.newProjectForm.get('project_id').value
    );
  }

  onDownloadAll(): void {
    this.downloadService.onDownloadAll(
      this.newProjectForm.get('project_id').value
    );
  }

  onCheckGenerateTemplateOrIndex(
    event: MatCheckboxChange,
    generateTemplate: number
  ): void {
    debugger
    switch (generateTemplate) {
      case 0:
        if (!event.checked) {
          this.disableControls(this.template_properties);
          this.disableControl(this.isGenerateDedicatedComponentsTemplate);
          this.disableControl(this.isSeparateMappingsAndSettings);
          // TODO: is_generate_dedicated_components_template,is_separate_mappings_and_settings
          this.clearValidators(this.template_nameForTemplate);
          this.clearValidators(this.index_patternsForTemplate);
          this.cdr.markForCheck();
        } else {
          this.setValidators(this.template_nameForTemplate);
          this.setValidators(this.index_patternsForTemplate);
          this.enableControls(this.template_properties);
          this.enableControl(this.isGenerateDedicatedComponentsTemplate);
          this.enableControl(this.isSeparateMappingsAndSettings);
          // this.isDisableButton = false;
          this.cdr.detectChanges();
        }
        break;
      case 1:
        if (!event.checked) {
          this.clearValidators(this.template_nameForIndex);
          this.disableControls(this.index_properties);
          this.cdr.markForCheck();
        } else {
          this.setValidators(this.template_nameForIndex);
          this.enableControls(this.index_properties);
          this.cdr.markForCheck();
        }
        break;
    }
    if (
      this.is_generate_template.value === false &&
      this.is_generate_index.value === false
    ) {
      this.isDisableButton = true;
    }
  }

  disableControls(properties: FormGroup): void {
    Object.keys(properties.controls).forEach((key) => {
      properties.get(key).disable();
      this.cdr.detectChanges();
    });
  }
  disableControl(control: AbstractControl): void {
    control.disable();
    this.cdr.detectChanges();
  }
  enableControl(control: AbstractControl): void {
    control.enable();
    this.cdr.detectChanges();
  }


  enableControls(properties: FormGroup): void {
    Object.keys(properties.controls).forEach((key) => {
      properties.get(key).enable();
      this.cdr.detectChanges();
    });
  }

  setValidators(control: AbstractControl): void {
    control.setValidators(Validators.required);
  }

  clearValidators(control: AbstractControl): void {
    control.clearValidators();
  }

  onDeleteProject(project_id: string): void {
    this.isShowYesNoDialog = true;
  }

  onYes(event: boolean): void {
    this.cdr.markForCheck();
    this.isShowYesNoDialog = event;
    this.apiService.deleteProject(this.project_id).subscribe(
      (res) => {
        if (res) {
          this.toastr.success('Project was successfully deleted!', '');
          this.router.navigate(['/projects_monitoring']);
        } else {
          this.toastr.error('Something went wrong!', '');
        }
      },
      (err) => {
        this.toastr.error(`Server not responding! Please try again later`, '', {
          positionClass: 'toast-top-right',
        });
      }
    );
  }

  onNo(event: boolean): void {
    this.isShowYesNoDialog = event;
  }

  onCloseErrorDialog(event: boolean): void {
    this.isShowErrorDialog = event;
  }

  onCheckAddAllFieldsForTemplate(event: MatCheckboxChange): void {
    if (event.checked) {
      this.is_add_fields_from_at_least_one_time_used_root_level?.setValue(
        false
      );
      this.cdr.markForCheck();
    }
  }

  onCheckAddOnlyUsedTopLevelFieldsForTemplate(event: MatCheckboxChange): void {
    if (event.checked) {
      this.is_add_all_fields.patchValue(false);
    }
  }

  onCheckAddOnlyUsedTopLevelFieldsForIndex(event: MatCheckboxChange): void {
    if (event.checked) {
      this.is_add_all_fieldsForIndex.patchValue(false);
    }
  }

  onCheckAddAllFieldsForIndex(event: MatCheckboxChange): void {
    if (event.checked) {
      this.is_add_fields_from_at_least_one_time_used_root_levelForIndex.patchValue(
        false
      );
    }
  }

  ngOnDestroy(): void {
    this.destroyed$.next(true);
    this.destroyed$.complete();
  }

  disableButton(): boolean {
    if (
      this.is_done?.value === false ||
      this.is_failed?.value === true ||
      this.newProjectForm.get('actions').get('is_generate_index').value ===
      false ||
      this.newProjectForm.get('actions').get('is_generate_template').value ===
      false
    ) {
      return (this.isDisableButton = true);
    } else if (
      this.newProjectForm.get('actions').get('is_generate_index').value ===
      false ||
      this.newProjectForm.get('actions').get('is_generate_template').value ===
      false
    ) {
      return (this.isDisableButton = false);
    }
  }

  showIcon(checked: boolean, source: AbstractControl): boolean {
    if (checked || source.value === true) {
      return true;
    }
  }

  onCheckAddFieldsFromExistingTemplate(event: MatCheckboxChange, control: AbstractControl): void {
    if (event.checked) {
      this.isDisableButton = true;
      control.setValidators([Validators.required]);
    } else {
      this.isDisableButton = false;
      control.clearValidators();
    }
  }


}
