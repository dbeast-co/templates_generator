import {ChangeDetectorRef, Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import { ApiService } from '../../../../shared/api.service';
import { MatTableDataSource } from '@angular/material/table';
import { IProjectMonitoring } from '../../../../models/project-monitoring';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { DownloadService } from '../../../../shared/download.service';
import { interval, Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import {MatSort,Sort} from '@angular/material/sort';

@Component({
  selector: 'yl-projects-monitoring',
  templateUrl: './projects-monitoring.component.html',
  styleUrls: ['./projects-monitoring.component.scss'],
})
export class ProjectsMonitoringComponent implements OnInit, OnDestroy {
  displayedColumnsForSourceProjectMonitoringTable: string[] = [
    'project_name',
    'start_time',
    'end_time',
    'docs_in_index',
    'docs_for_analyze',
    'analyzed_docs',
    'progress',
    'status',
    'download_buttons',
    'action_buttons',
  ];
  sourceProjectMonitoring: MatTableDataSource<IProjectMonitoring> =
    new MatTableDataSource<IProjectMonitoring>();
  isShowYesNoDialog: boolean = false;
  project_id: string;
  private subscription: Subscription = new Subscription();
  @ViewChild(MatSort) sort: MatSort;
  constructor(
    private apiService: ApiService,
    private router: Router,
    private toastr: ToastrService,
    private cdr: ChangeDetectorRef,
    private downloadService: DownloadService,
    private window: Window
  ) {}

  ngOnInit(): void {
    this.subscription.add(
      this.apiService.getSavedProjectsForMonitoring().subscribe((projects) => {
        this.sourceProjectMonitoring =
          new MatTableDataSource<IProjectMonitoring>(projects);
        this.sourceProjectMonitoring.sort = this.sort;
        this.getProjectsMonitoring();
        this.cdr.markForCheck();
      })
    );
  }

  getProjectsMonitoring(): void {
    this.subscription.add(
      interval(10000)
        .pipe(switchMap(() => this.apiService.getSavedProjectsForMonitoring()))
        .subscribe((projects) => {
          console.log('refresh', projects);
          this.sourceProjectMonitoring.data = projects;
          this.cdr.markForCheck();
        })
    );
  }

  onDeleteProject(element: IProjectMonitoring): void {
    this.isShowYesNoDialog = true;
    this.project_id = element.project_id;
  }

  onYes(event: boolean): void {
    this.isShowYesNoDialog = event;
    this.apiService.deleteProject(this.project_id).subscribe(
      (res) => {
        if (res) {
          this.sourceProjectMonitoring.data =
            this.sourceProjectMonitoring.data.filter(
              (data) => data.project_id !== this.project_id
            );
          this.toastr.success('Project was successfully deleted!', '');
          this.cdr.markForCheck();
        } else {
          this.toastr.error('Something went wrong!', '');
        }
      },
      (error) => {
        this.toastr.error(`Server not responding! Please try again later`, '', {
          positionClass: 'toast-top-right',
        });
      }
    );
  }

  onClose(event: boolean): void {
    this.isShowYesNoDialog = event;
  }

  onDownloadTemplate(element: IProjectMonitoring): void {
    this.downloadService.onDownloadTemplate(element.project_id);
  }

  onDownloadChangeLog(element: IProjectMonitoring): void {
    this.downloadService.onDownloadLogs(element.project_id);
  }

  onDownloadIndex(element: IProjectMonitoring): void {
    this.downloadService.onDownloadIndex(element.project_id);
  }

  onDownloadAll(element: IProjectMonitoring): void {
    this.downloadService.onDownloadAll(element.project_id);
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  announceSortChange(event: Sort): void {
    this.sourceProjectMonitoring.sort = this.sort;
    if (event.active === 'status') {
      this.sourceProjectMonitoring.data.sort((a, b) => {
        if (a.project_status < b.project_status) {
          return -1;
        } else if (a.project_status > b.project_status) {
          return 1;
        }
        return 0;
      });
    }
    this.cdr.markForCheck();
  }
}
