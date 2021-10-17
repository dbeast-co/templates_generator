import { ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { ApiService } from '../../../../shared/api.service';
import { ISavedProject } from '../../../../models/i-saved-project';
import { ToastrService } from 'ngx-toastr';
import { NewProjectConfigurationStore } from '../../../new-project-configuration/components/store/new-project-configuration-store';
import { takeUntil } from 'rxjs/operators';
import { ReplaySubject } from 'rxjs';

@Component({
  selector: 'yl-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss'],
})
export class HeaderComponent implements OnInit, OnDestroy {
  headerTitle: string = '';
  savedProjects: Array<ISavedProject>;
  private destroyed$: ReplaySubject<boolean> = new ReplaySubject(1);

  constructor(
    private apiService: ApiService,
    private cdr: ChangeDetectorRef,
    private toastr: ToastrService,
    private state: NewProjectConfigurationStore
  ) {}

  ngOnInit(): void {}

  /**
   * Click on 'Hamburger' icon to open the menu
   */
  onOpenMenu(): void {
    this.apiService
      .getSavedProjects()
      .pipe(takeUntil(this.destroyed$))
      .subscribe(
        (data) => {
          this.savedProjects = data;
          // console.log(this.savedProjects);
          this.cdr.markForCheck();
        },
        (error) => {
          this.toastr.error(
            `Server not responding!Please try again later`,
            '',
            {
              positionClass: 'toast-top-right',
            }
          );
        }
      );
  }

  /**
   * Click on 'Create new project'
   */
  onCreateNewProject(): void {
    this.apiService.getNewProjectConfiguration().subscribe((data) => {
      this.state.getNewConfigState();
    });
    this.cdr.markForCheck();
  }

  /**
   * Click on 'Open saved project'
   * @param project_id: string
   */
  onOpenSavedProject(project_id: string): void {}

  ngOnDestroy(): void {
    this.destroyed$.next(true);
    this.destroyed$.complete();
  }

  /**
   * Click on project monitoring and navigate to 'Project_monitoring'
   */
  onProjectsMonitoring(): void {}
}
