import { Injectable } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { DeleteConfirmationComponent } from '../delete-confirmation/delete-confirmation.component';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class DialogService {
  constructor(private dialog: MatDialog) {}

  openDeleteConfirmation(): Observable<boolean> {
    const dialogRef = this.dialog.open<DeleteConfirmationComponent, any, boolean>(
      DeleteConfirmationComponent
    );

    return dialogRef.afterClosed();
  }
}
