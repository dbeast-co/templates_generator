import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { ApiService } from '../../../../shared/api.service';
import { ProjectFormService } from '../../services/project-form.service';
import { FormArray, FormControl, FormGroup } from '@angular/forms';
import { SelectionModel } from '@angular/cdk/collections';
import { MatSort } from '@angular/material/sort';
import { MatCheckboxChange } from '@angular/material/checkbox';
import { switchMap } from 'rxjs/operators';

interface ISourceIndexList {
  template_name: string;
  is_checked: boolean;
}

@Component({
  selector: 'app-templates-table',
  templateUrl: './templates-table.component.html',
  styleUrls: ['./templates-table.component.scss'],
})
export class TemplatesTableComponent implements OnInit {
  projectForm: FormGroup;
  // project: IProjectModel = null;
  displayedColumnsForIndexPatternNamesTable: string[] = [
    'checkboxIndexPattern',
    'index_name',
    'viewIndexPattern',
  ];

  sourceIndexListMatSource: MatTableDataSource<ISourceIndexList>;
  selectionIndexList = new SelectionModel<ISourceIndexList>(true, []);
  source_cluster_status: string = '';
  destination_cluster_status: string = '';
  maxEmptyRows: number = 12;
  showViewButtonInTables: boolean = false;
  emptyRowForSourceIndexList: ISourceIndexList = {
    is_checked: false,
    template_name: '',
  };

  @ViewChild('fileSourceRef') fileSourceRef: ElementRef;
  @ViewChild('fileDestinationRef') fileDestinationRef: ElementRef;
  @ViewChild('mainCheckboxForIndexList') mainCheckboxForIndexList: ElementRef;
  source_status_on_reload_page: string = '';
  destination_status_on_reload_page: string = '';
  isOnFilter: boolean = false;
  @ViewChild('sourceClusterStatus') sourceClusterStatusRef: ElementRef;
  @ViewChild('destinationClusterStatusRef')
  destinationClusterStatus: ElementRef;
  @ViewChild(MatSort) sort: MatSort;
  isOnLoading: boolean = false;
  legacy_templates_list: FormArray = new FormArray([]);
  filterTableValue: string = '';
  originalSourceIndex: Array<ISourceIndexList> = [];

  constructor(
    private apiService: ApiService,
    private projectFormService: ProjectFormService
  ) {}

  ngOnInit() {
    this.projectForm = this.projectFormService.projectForm;

    this.sourceIndexListMatSource = new MatTableDataSource<ISourceIndexList>(
      []
    );
    for (let i = 0; i < this.maxEmptyRows; i++) {
      this.sourceIndexListMatSource.data = [
        ...this.sourceIndexListMatSource.data,
        this.emptyRowForSourceIndexList,
      ];
    }
    this.projectFormService.getTemplatesClicked$
      .pipe(
        switchMap((clicked) => {
          if (clicked) {
            return this.apiService.getLegacyTemplates(this.projectForm);
          } else {
            return [];
          }
        })
      )
      .subscribe({
        next: (res) => {
          this.originalSourceIndex = res.legacy_templates_list;
          this.sourceIndexListMatSource.data = [];
          this.legacy_templates_list = res.legacy_templates_list;

          for (let i = 0; i < this.maxEmptyRows; i++) {
            this.sourceIndexListMatSource.data = [...res.legacy_templates_list];
          }
          if (res.legacy_templates_list.length <= this.maxEmptyRows) {
            for (
              let i = 0;
              i < this.maxEmptyRows - res.legacy_templates_list.length;
              i++
            ) {
              this.sourceIndexListMatSource.data = [
                ...this.sourceIndexListMatSource.data,
                this.emptyRowForSourceIndexList,
              ];
            }
          }
        },
      });

    this.source_cluster_status = '';
    this.source_status_on_reload_page = '';
    this.destination_cluster_status = '';
    this.destination_status_on_reload_page = '';
    this.isOnLoading = true;
  }

  /**
   * Check status on change page
   */

  isAllSelectedIndexList() {
    const numSelected = this.selectionIndexList.selected.length;
    const numRows = this.sourceIndexListMatSource.data.length;
    if (this.sourceIndexListMatSource.filteredData.length > 0) {
      const numFilterSelected = this.selectionIndexList.selected.length;
      const numFilteredRows = this.sourceIndexListMatSource.filteredData.length;
      return numFilterSelected === numFilteredRows;
    }
    return numSelected === numRows;
  }

  masterToggleIndexList(event: MatCheckboxChange) {
    if (event.checked) {
      const allSelected = this.sourceIndexListMatSource.data.map((row) => {
        row.is_checked = true;
        return row;
      });
      this.selectionIndexList = new SelectionModel<ISourceIndexList>(
        true,
        allSelected
      );
      this.legacy_templates_list.patchValue(allSelected);
    } else {
      const allUnselected = this.sourceIndexListMatSource.data.map((row) => {
        row.is_checked = false;
        return row;
      });
      this.selectionIndexList = new SelectionModel<ISourceIndexList>(
        true,
        allUnselected
      );
      this.legacy_templates_list.patchValue(allUnselected);
    }
  }

  checkRowOfSourceIndexTable(event: MatCheckboxChange, row: ISourceIndexList) {
    const checkedRowIndex = this.sourceIndexListMatSource.data.find(
      (a) => a.template_name === row.template_name
    );
    if (event.checked) {
      this.legacy_templates_list =
        this.projectFormService.legacy_templates_list;
      this.selectionIndexList.selected.push(checkedRowIndex);
      // this.legacy_templates_list.setValue([]);
      this.legacy_templates_list.push(
        new FormControl({
          template_name: row.template_name,
          is_checked: event.checked,
        })
      );
    } else {
      const index = this.selectionIndexList.selected.findIndex(
        () => checkedRowIndex.is_checked === event.checked
      );
      this.selectionIndexList.selected.splice(index, 1);

      this.legacy_templates_list.removeAt(index);
    }
  }

  applyFilterForSourceIndexTable(event: KeyboardEvent) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.sourceIndexListMatSource.filter = filterValue.trim().toLowerCase();
    this.isOnFilter = (event.target as HTMLInputElement).value !== '';
  }
}
