import { Injectable } from '@angular/core';
import { ApiService } from './api.service';
import * as FileSaver from 'file-saver';
import { HttpResponse } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class DownloadService {
  constructor(private apiService: ApiService) {}

  onDownloadTemplate(project_id): void {
    this.apiService
      .downloadTemplate(project_id)
      .subscribe((data) => this.saveFile(data));
  }
  onDownloadIndex(project_id): void {
    this.apiService
      .downloadIndex(project_id)
      .subscribe((data) => this.saveFile(data));
  }

  onDownloadAll(project_id): void {
    this.apiService
      .downloadAll(project_id)
      .subscribe((data) => this.saveFileZip(data));
  }
  onDownloadLogs(project_id): void {
    this.apiService
      .downloadLogs(project_id)
      .subscribe((data) => this.saveFile(data));
  }

  saveFile(response: HttpResponse<Blob>): void {
    const contentDisposition = response.headers.get('content-disposition');
    const filename = contentDisposition
      .split(';')[1]
      .split('filename')[1]
      .split('=')[1]
      .trim();
    const blob = new Blob([response.body], { type: 'application/json' });
    FileSaver.saveAs(blob, `${filename}`);
  }
  saveFileZip(response: HttpResponse<Blob>): void {
    const contentDisposition = response.headers.get('content-disposition');
    const filename = contentDisposition
      .split(';')[1]
      .split('filename')[1]
      .split('=')[1]
      .trim();
    const blob = new Blob([response.body], { type: 'application/zip' });
    FileSaver.saveAs(blob, `${filename}`);
  }
}
