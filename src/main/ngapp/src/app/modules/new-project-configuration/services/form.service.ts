import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Injectable } from '@angular/core';
import {
  IExistingTemplateActions,
  ProjectConfigurationModel,
} from '../../../models/project-configuration.model';

@Injectable()
export class FormService {
  private projectConfigurationForm: FormGroup;

  constructor(private fb: FormBuilder) {
    this.projectConfigurationForm = this.fb.group({});
  }

  getProjectConfigurationForm(project: ProjectConfigurationModel): FormGroup {
    console.log(project);
    const hostRegex = '(http|https):\\/\\/((\\w|-|\\d|_|\\.)+)\\:\\d{2,5}';
    this.projectConfigurationForm = this.fb.group({
      project_id: [project?.project_id],
      project_name: [project?.project_name, Validators.required],
      actions: this.fb.group({
        is_generate_index: [project?.actions.is_generate_index],
        is_generate_index_template: [project?.actions.is_generate_index_template],
        is_generate_template: [project?.actions.is_generate_template],
        is_generate_legacy_template: [project?.actions.is_generate_legacy_template],

        is_generate_dedicated_components_template: [project?.actions.is_generate_dedicated_components_template],
        is_separate_mappings_and_settings: [project?.actions.is_separate_mappings_and_settings],


      }),
      input: this.fb.group({
        input_settings: this.fb.group({
          index_for_analyze: [
            project?.input.input_settings.index_for_analyze,
            Validators.required,
          ],
          max_docs_for_analyze: [
            project?.input.input_settings.max_docs_for_analyze,
          ],
          scroll_size: [project?.input.input_settings.scroll_size],
        }),
        source_cluster: this.fb.group({
          authentication_enabled: [
            project?.input.source_cluster.authentication_enabled,
          ],
          es_host: [
            project?.input.source_cluster.es_host,
            Validators.pattern(hostRegex),
          ],
          password: [
            project?.input.source_cluster.password,
            Validators.required,
          ],
          ssl_enabled: [project?.input.source_cluster.ssl_enabled],
          ssl_file: [project?.input.source_cluster.ssl_file],
          status: [project?.input.source_cluster.status],
          username: [
            project?.input.source_cluster.username,
            Validators.required,
          ],
        }),
      }),
      mapping_changes_log: [project?.mapping_changes_log],
      output: this.fb.group({
        index_properties: this.fb.group({
          alias: [project?.output.index_properties.alias],
          is_add_fields_from_existing_template: [
            project?.output.index_properties
              .is_add_fields_from_existing_template,
          ],
          is_add_normalizer_to_keyword_fields: [
            project?.output.index_properties
              .is_add_normalizer_to_keyword_fields,
          ],
          existing_template_actions: this.getExistingProperties(
            project?.output.index_properties.existing_template_actions
          ),
          index_name: [project?.output.index_properties.index_name],
          index_settings: this.fb.group({
            codec: [project?.output.index_properties.index_settings.codec],
            number_of_replicas: [
              project?.output.index_properties.index_settings
                .number_of_replicas,
            ],
            number_of_shards: [
              project?.output.index_properties.index_settings.number_of_shards,
            ],
            refresh_interval: [
              project?.output.index_properties.index_settings.refresh_interval,
            ],
          }),
        }),
        template_properties: this.fb.group({
          alias: [project?.output.template_properties.alias],
          template_name: [
            project?.output.template_properties.template_name || '',
          ],
          order: [project?.output.template_properties.order],
          is_add_fields_from_existing_template: [
            project?.output.template_properties
              .is_add_fields_from_existing_template,
          ],
          is_add_normalizer_to_keyword_fields: [
            project?.output.template_properties
              .is_add_normalizer_to_keyword_fields,
          ],

          existing_template_actions: this.getExistingProperties(
            project?.output.template_properties.existing_template_actions
          ),
          index_patterns: [project?.output.template_properties.index_patterns],
          index_settings: this.fb.group({
            codec: [project?.output.template_properties.index_settings.codec],
            number_of_replicas: [
              project?.output.template_properties.index_settings
                .number_of_replicas,
            ],
            number_of_shards: [
              project?.output.template_properties.index_settings
                .number_of_shards,
            ],
            refresh_interval: [
              project?.output.template_properties.index_settings
                .refresh_interval,
            ],
          }),
        }),
      }),
      status: this.fb.group({
        analyzed_docs: [project?.status.analyzed_docs],
        docs_for_analyze: [project?.status.docs_for_analyze],
        docs_in_index: [project?.status.docs_in_index],
        execution_progress: [project?.status.execution_progress],
        is_done: [project?.status.is_done],
        is_in_active_process: [project?.status.is_in_active_process],
        project_status: [project?.status.project_status],
      }),
    });

    return this.projectConfigurationForm;
  }

  disableControls(properties: FormGroup): void {
    Object.keys(properties.controls).forEach((key) => {
      properties.get(key).disable();
    });
  }

  getExistingProperties(
    action: IExistingTemplateActions | undefined
  ): FormGroup {
    return this.fb.group({
      add_fields_from_existing_template_name: [
        action?.add_fields_from_existing_template_name,
      ],
      is_add_all_fields: [action?.is_add_all_fields],
      is_add_dynamic_mapping: [action?.is_add_dynamic_mapping],
      is_add_fields_from_at_least_one_time_used_root_level: [
        action?.is_add_fields_from_at_least_one_time_used_root_level,
      ],
      is_add_settings: [action?.is_add_settings],
      is_generate_dedicated_none_existing_fields_template: [
        action?.is_generate_dedicated_none_existing_fields_template,
      ],
      is_ignore_type_conflicts: [action?.is_ignore_type_conflicts],

      is_replace_existing_field_types: [
        action?.is_replace_existing_field_types,
      ],
    });
  }
}
