import {Injectable} from '@angular/core';
import {Observable, Subject} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class HeaderService {
  private subject: Subject<string> = new Subject<string>();

  constructor() {
  }

  getHeaderTitle(): Observable<string> {
    return this.subject.asObservable();
  }

  setHeaderTitle(title: string): void {
    this.subject.next(title);
  }
}
