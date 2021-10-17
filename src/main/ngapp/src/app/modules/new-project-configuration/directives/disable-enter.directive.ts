import {Directive, ElementRef, HostListener} from '@angular/core';

@Directive({
  selector: '[ylDisableEnter]'
})
export class DisableEnterDirective {

  constructor(private el: ElementRef) {
  }
  @HostListener('keydown', ['$event'])
  onInput(event: KeyboardEvent): boolean {
    if (event.keyCode === 13) {
      event.preventDefault();
      return false;
    }
  }

}
