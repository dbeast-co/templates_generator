import { Component, OnInit } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { HeaderService } from '../../shared/header.service';

@Component({
  selector: 'app-legacy-templates-conveter',
  templateUrl: './legacy-templates-conveter.component.html',
  styleUrls: ['./legacy-templates-conveter.component.scss'],
})
export class LegacyTemplatesConveterComponent implements OnInit {
  isLoading: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);

  constructor(private headerService: HeaderService) {}

  ngOnInit(): void {
    this.headerService.setHeaderTitle('Legacy templates converter');
  }
}
