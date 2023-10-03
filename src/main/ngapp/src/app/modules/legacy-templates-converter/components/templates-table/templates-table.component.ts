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
  legacy_templates_list: FormArray;

  constructor(
    private apiService: ApiService,
    private projectFormService: ProjectFormService
  ) {}

  ngOnInit() {
    this.projectForm = this.projectFormService.projectForm;
    this.legacy_templates_list = this.projectFormService.legacy_templates_list;
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
          if (res.legacy_templates_list.length > 0) {
            // this.sourceIndexListMatSource =
            //   new MatTableDataSource<ISourceIndexList>(
            //     res.legacy_templates_list
            //   );
            this.sourceIndexListMatSource.data = res.legacy_templates_list;
            for (let i = 0; i < this.maxEmptyRows; i++) {
              this.sourceIndexListMatSource.data = [
                ...this.sourceIndexListMatSource.data,
                this.emptyRowForSourceIndexList,
              ];
            }
            this.legacy_templates_list.patchValue(res.legacy_templates_list);
          } else {
            for (let i = 0; i < this.maxEmptyRows; i++) {
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
    if (this.isAllSelectedIndexList()) {
      this.selectionIndexList.clear();
    } else {
      this.sourceIndexListMatSource.data.forEach((row) =>
        this.selectionIndexList.select(row)
      );
    }
    if (
      this.selectionIndexList.selected.length ===
      this.sourceIndexListMatSource.data.length
    ) {
      const selectedIndexList = this.selectionIndexList.selected.map((item) => {
        item.is_checked = true;
        return item;
      });
      this.legacy_templates_list.patchValue(selectedIndexList);
    }
    if (this.selectionIndexList.selected.length === 0) {
      const unSelectedIndexList = this.sourceIndexListMatSource.data.map(
        (item) => {
          item.is_checked = false;
          return item;
        }
      );
      this.legacy_templates_list.patchValue(unSelectedIndexList);
    }
  }

  checkRowOfSourceIndexTable(event: MatCheckboxChange, row: ISourceIndexList) {
    const checkedRowIndex = this.sourceIndexListMatSource.data.find(
      (a) => a.template_name === row.template_name
    );
    if (event.checked) {
      this.legacy_templates_list.push(
        new FormControl({
          template_name: row.template_name,
          is_checked: event.checked,
        })
      );
    } else {
      const index = this.legacy_templates_list.controls.findIndex(
        (a) => a.value.template_name === row.template_name
      );
      this.legacy_templates_list.removeAt(index);
    }
    return (checkedRowIndex.is_checked = event.checked);
  }

  onSelectAllAfterFilter(event: MatCheckboxChange) {
    // this.cdr.markForCheck();
    // if (event.checked) {
    //   this.selectionIndexList = new SelectionModel<ISourceIndexList>(
    //     true,
    //     this.sourceIndexListMatSource.filteredData
    //   );
    //   this.selectionIndexList.selected.map((item) => {
    //     item.is_checked = true;
    //     return item;
    //   });
    //
    //   this.source_index_list.patchValue(this.selectionIndexList.selected);
    //   const filteredSourceIndexList = this.source_index_list.controls.filter(
    //     (a) => a.value !== null
    //   );
    //   const newSourceIndexList: UntypedFormArray = new UntypedFormArray([]);
    //   filteredSourceIndexList.forEach((item) => {
    //     newSourceIndexList.push(item);
    //   });
    //
    //   this.source_index_list.patchValue(newSourceIndexList.value);
    // } else {
    //   this.selectionIndexList.selected.map((item) => {
    //     item.is_checked = false;
    //     return item;
    //   });
    //   this.source_index_list.patchValue(this.selectionIndexList.selected);
    // }
  }

  onRun() {
    console.group('%c GROUP', 'color:#84B59F');
    console.log(this.projectForm.value);
    console.groupEnd();
  }
}
