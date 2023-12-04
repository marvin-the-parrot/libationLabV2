import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA} from "@angular/material/dialog";

export enum ConfirmationDialogMode {
  Delete,
  MakeHost,
  Leave
  // add more modes here if needed
}

@Component({
  selector: 'app-delete-confirmation',
  templateUrl: './confirmation-dialog.component.html',
  styleUrls: ['./confirmation-dialog.component.scss']
})
export class ConfirmationDialogComponent {

  cancelBtnText = 'Cancel';
  confirmBtnText = 'Confirm';

  dialogHeader = "Are you sure?";
  dialogText = "";
  backgroundColor = '#f95959';

  constructor(@Inject(MAT_DIALOG_DATA) public data: { mode: ConfirmationDialogMode }) {

    switch (data.mode) {
      case ConfirmationDialogMode.Delete:
        this.dialogHeader = 'Confirm remove user'
        this.dialogText = "Are you sure you want to remove this user from the group?"
        this.confirmBtnText = 'Remove user';
        this.backgroundColor = '#f95959';
        break;
      case ConfirmationDialogMode.MakeHost:
        this.dialogHeader = "Confirm new host";
        this.dialogText = "Are you sure you want to make this user the new host?"
        this.confirmBtnText = 'Make user host';
        this.backgroundColor = '#ffb743';
        break;
      case ConfirmationDialogMode.Leave:
        this.dialogHeader = "Confirm leave group";
        this.dialogText = "Are you sure you want to leave this group?"
        this.confirmBtnText = 'Leave group';
        this.backgroundColor = '#f95959';
        break;
      default:
        console.error("Invalid confirmation dialog mode");
        break;
    }
  }

  protected readonly ConfirmationDialogMode = ConfirmationDialogMode;
}
