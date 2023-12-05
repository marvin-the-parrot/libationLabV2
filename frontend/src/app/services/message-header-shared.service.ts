import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class MessageHeaderSharedService {

  // Use a Subject to create an observable that other components can subscribe to
  private reloadSubject = new Subject<void>();

  // Observable for components to subscribe to
  reload$ = this.reloadSubject.asObservable();

  // Method to trigger a reload
  triggerReload() {
    this.reloadSubject.next();
  }
}
