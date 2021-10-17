import {Component, EventEmitter, OnInit, Output} from '@angular/core';

@Component({
  selector: 'yl-yes-no',
  templateUrl: './yes-no.component.html',
  styleUrls: ['./yes-no.component.scss']
})
export class YesNoComponent implements OnInit {
  @Output() isYesEmit: EventEmitter<boolean> = new EventEmitter<boolean>();
  @Output() isNoEmit: EventEmitter<boolean> = new EventEmitter<boolean>();
  @Output() isCloseDialog: EventEmitter<boolean> = new EventEmitter<boolean>();
  constructor() { }

  ngOnInit(): void {
  }
  onYes(): void {
    this.isYesEmit.emit(false);
  }



  onNo(): void {
    this.isNoEmit.emit(false);
  }

  onCloseDialog(): void {
    this.isCloseDialog.emit(false);
  }

}
