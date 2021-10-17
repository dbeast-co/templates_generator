export interface IProjectMonitoring {
  project_id: string;
  project_name: string;
  execution_progress: number;
  project_status: string;
  docs_in_index: number;
  docs_for_analyze: number;
  analyzed_docs: number;
  start_time: string;
  end_time: string;
}
