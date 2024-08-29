import {TemplateSource} from './template-source';

export interface LegacyTemplate {
  project_id: string;
  connection_settings: {
    es_host: string;
    authentication_enabled: boolean;
    username: string;
    password: string;
    ssl_enabled: boolean;
    ssl_file: string | null;
  }
  is_generate_dedicated_components_template: boolean;
  is_separate_mappings_and_settings: boolean;
  legacy_templates_list: Array<TemplateSource>
}
