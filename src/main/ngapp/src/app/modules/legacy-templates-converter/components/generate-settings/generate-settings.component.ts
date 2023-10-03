import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ProjectFormService } from '../../services/project-form.service';
import { ApiService } from '../../../../shared/api.service';

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
    private apiService: ApiService
  ) {}

  ngOnInit(): void {
    this.projectForm = this.projectFormService.projectForm;
  }

  onRun() {
    this.isLoadingEmit.emit(true);
    this.apiService
      .runTemplatesConverter(this.projectForm.value)
      .subscribe((res) => {
        this.isLoadingEmit.emit(false);
      });
  }
}
