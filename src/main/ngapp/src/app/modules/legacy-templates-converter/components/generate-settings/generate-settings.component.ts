import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ProjectFormService } from '../../services/project-form.service';
import { ApiService } from '../../../../shared/api.service';
import { ToastrService } from 'ngx-toastr';
import { DownloadService } from '../../../../shared/download.service';

@Component({
  selector: 'app-generate-settings',
  templateUrl: './generate-settings.component.html',
  styleUrls: ['./generate-settings.component.scss'],
})
export class GenerateSettingsComponent implements OnInit {
  projectForm: FormGroup;
  @Output() isLoadingEmit: EventEmitter<boolean> = new EventEmitter<boolean>(
    false
  );

  constructor(
    private projectFormService: ProjectFormService,
    private apiService: ApiService,
    private toastrService: ToastrService,
    private downloadService: DownloadService
  ) {}

  ngOnInit(): void {
    this.projectForm = this.projectFormService.projectForm;
  }

  onRun() {
    this.isLoadingEmit.emit(true);
    this.apiService.runTemplatesConverter(this.projectForm).subscribe(
      (res) => {
        console.group('%c SUCCESS', 'color:#84B59F');
        console.log('SUCCESS');
        console.groupEnd();
        this.downloadService.saveFileZip(res);
        this.isLoadingEmit.emit(false);
        this.toastrService.success('', 'Converted  successfully');
      },
      (error) => {
        this.isLoadingEmit.emit(false);
        this.toastrService.error('', 'Something  went wrong!!!');
      }
    );
  }
}
