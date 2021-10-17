import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';

@Component({
  selector: 'yl-error',
  templateUrl: './error.component.html',
  styleUrls: ['./error.component.scss']
})
export class ErrorComponent implements OnInit {
  @Input() errorMessage: string = '';
  @Output() isCloseEmit: EventEmitter<boolean> = new EventEmitter<boolean>();

  constructor() {
  }

  ngOnInit(): void {
  }

  onCloseDialog(): void {
    this.isCloseEmit.emit(false);
  }
}
