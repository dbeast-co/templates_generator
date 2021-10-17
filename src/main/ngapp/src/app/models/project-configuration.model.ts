export interface ProjectConfigurationModel {
  project_name: string;
  project_id: string;
  input: IInput;
  output: IOutput;
  status: IStatus;
  actions: IActions;
  mapping_changes_log: Array<string>;
}

export interface IInput {
  input_settings: IInputSettings;
  source_cluster: ISourceCluster;
}

export interface IInputSettings {
  index_for_analyze: string | null;
  max_docs_for_analyze: number;
  scroll_size: number;
}

export interface ISourceCluster {
  es_host: string;
  authentication_enabled: boolean;
  username: string | null;
  password: string | null;
  ssl_enabled: boolean;
  ssl_file: null;
  status: string;
}

export interface IOutput {
  template_properties: ITemplateProperties;
  index_properties: IIndexProperties;
}

export interface ITemplateProperties {
  alias: string | null;
  template_name: string | null;
  index_patterns: string;
  is_add_fields_from_existing_template: boolean;
  is_add_normalizer_to_keyword_fields: boolean;
  existing_template_actions: IExistingTemplateActions;
  order: number;
  index_settings: IIndexSettings;
}

export interface IIndexSettings {
  number_of_shards: number;
  number_of_replicas: number;
  refresh_interval: string;
  codec: string;
}

export interface IIndexProperties {
  alias: string | null;
  index_name: string;
  index_settings: IIndexSettings;
  is_add_fields_from_existing_template: boolean;
  is_add_normalizer_to_keyword_fields: boolean;
  existing_template_actions: IExistingTemplateActions;
}

export interface IStatus {
  execution_progress: number;
  project_status: string;
  is_done: boolean;
  is_failed: boolean;
  is_in_active_process: boolean;
  docs_in_index: number;
  is_stopped: boolean;
  docs_for_analyze: number;
  analyzed_docs: number;
}

export interface IActions {
  is_generate_template: boolean;
  is_generate_index: boolean;
}

export interface IClusterStatus {
  cluster_status: string;
}

export interface IExistingTemplateActions {
  add_fields_from_existing_template_name: string;
  is_add_all_fields: boolean;
  is_add_dynamic_mapping: boolean;
  is_add_fields_from_at_least_one_time_used_root_level: boolean;
  is_add_settings: boolean;
  is_generate_dedicated_none_existing_fields_template: boolean;
  is_ignore_type_conflicts: boolean;
  is_replace_existing_field_types: boolean;
}
