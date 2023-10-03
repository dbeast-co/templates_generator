import { Component, OnInit } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Component({
  selector: 'app-legacy-templates-conveter',
  templateUrl: './legacy-templates-conveter.component.html',
  styleUrls: ['./legacy-templates-conveter.component.scss'],
})
export class LegacyTemplatesConveterComponent implements OnInit {
  isLoading: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);

  constructor() {}

  ngOnInit(): void {}
}
