import { Injectable } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import {
  ConfirmationDialogMode,
  ConfirmationDialogComponent
} from '../confirmation-dialog/confirmation-dialog.component';
import { Observable, Subject } from 'rxjs';
import { OptionDialogComponent } from '../option-dialog/option-dialog.component';
import {AddIngredientDialogComponent} from "../add-ingredient-dialog/add-ingredient-dialog.component";
import {GenerateMenuDialogComponent} from "../generate-menu-dialog/generate-menu-dialog.component";

@Injectable({
  providedIn: 'root',
})
export class DialogService {
  private selectedOptionSubject = new Subject<string>();
  private dialogRef: MatDialogRef<OptionDialogComponent>;

  constructor(private dialog: MatDialog) {}

  /**
   * Opens a dialog that lets the user confirm an action
   * @param mode an enum that determines the type of confirmation dialog that should be opened
   */
  openConfirmationDialog(mode: ConfirmationDialogMode): Observable<boolean> {
    const dialogRef = this.dialog.open<ConfirmationDialogComponent, any, boolean>(
      ConfirmationDialogComponent,
      {
        data: { mode }
      }
    );

    return dialogRef.afterClosed();
  }

  /**
   * Opens a dialog that lets the host choose between the options "Make Host" and "Remove from Group" for a given user
   *
   * @param position the position of the dialog
   * @return true if the user should be removed from the group, false if the user should be made host, undefined if the dialog was closed
   */
  openOptionDialog(position: { top: string; left: string; }): Observable<Boolean> {
    const dialogRef = this.dialog.open<OptionDialogComponent, any, boolean>(
      OptionDialogComponent, {
        position: position
      }
    );

    return dialogRef.afterClosed();
  }

  /**
   * Opens a dialog that shows the user ingredients with which the group would be able to mix more cocktails, and lets the user add one of them to the group
   * @param groupId the id of the group for which the best ingredient should be searched
   */
  openAddIngredientDialog(groupId: number): Observable<boolean> {
    const dialogRef = this.dialog.open<AddIngredientDialogComponent, any, boolean>(
      AddIngredientDialogComponent,
      {
        height: '80%', // makes sure the dialog is big enough
        data: { groupId }
      }
    );

    dialogRef.componentInstance.result.subscribe((res: boolean) => {
      dialogRef.close(res);
    });

    return dialogRef.afterClosed();
  }

  /**
   * Opens a dialog that shows the user menues and lets the user add one of them to the group
   * @param groupId the id of the group for which the menues should be generated
   */
  openGenerateMenuDialog(groupId: number, numberOfCocktails: number): Observable<boolean> {
    const dialogRef = this.dialog.open<GenerateMenuDialogComponent, any, boolean>(
      GenerateMenuDialogComponent,
      {
        height: '80%', // makes sure the dialog is big enough
        data: { groupId, numberOfCocktails }
      }
    );

    dialogRef.componentInstance.result.subscribe((res: boolean) => {
      dialogRef.close(res);
    });

    return dialogRef.afterClosed();
  }

}
