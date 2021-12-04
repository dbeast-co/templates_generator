import {Directive, Input, OnInit, Optional} from '@angular/core';

@Directive({
  selector: '[ylDisableControl]',
})
export class DisableControlDirective {
  @Input('ylDisableControl') set disableControl(condition: boolean) {
    const action = condition ? 'disable' : 'enable';
    console.log(action);
    console.log(this.ngControl);
    if (action === 'disable' && this.ngControl) {
      this.ngControl.disabled = true;
    }
  }


  constructor(@Optional() private ngControl: HTMLButtonElement) {}
}
