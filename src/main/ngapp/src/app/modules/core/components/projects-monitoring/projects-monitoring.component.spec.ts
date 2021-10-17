import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProjectsMonitoringComponent } from './projects-monitoring.component';

describe('ProjectsMonitoringComponent', () => {
  let component: ProjectsMonitoringComponent;
  let fixture: ComponentFixture<ProjectsMonitoringComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ProjectsMonitoringComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ProjectsMonitoringComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
