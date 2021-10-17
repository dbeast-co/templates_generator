import { TestBed } from '@angular/core/testing';

import { Store } from './store';

describe('MenuService', () => {
  let service: Store;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Store);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
