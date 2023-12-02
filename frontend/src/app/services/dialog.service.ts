import { Injectable } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { DeleteConfirmationComponent } from '../delete-confirmation/delete-confirmation.component';
import { Observable, Subject } from 'rxjs';
import { OptionDialogComponent } from '../option-dialog/option-dialog.component';

@Injectable({
  providedIn: 'root',
})
export class DialogService {
  private selectedOptionSubject = new Subject<string>();
  private dialogRef: MatDialogRef<OptionDialogComponent>;

  constructor(private dialog: MatDialog) {}

  openDeleteConfirmation(): Observable<boolean> {
    const dialogRef = this.dialog.open<DeleteConfirmationComponent, any, boolean>(
      DeleteConfirmationComponent
    );

    return dialogRef.afterClosed();
  }

  openOptionDialog(): Observable<Boolean> {
    const dialogRef = this.dialog.open<OptionDialogComponent, any, boolean>(
      OptionDialogComponent
    );

    return dialogRef.afterClosed();
  }

}
