import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA} from "@angular/material/dialog";

export enum ConfirmationDialogMode {
  RemoveUser,
  MakeHost,
  Leave,
  DeleteAccount,
  RemoveUsers,
  DeleteGroup
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
      case ConfirmationDialogMode.RemoveUser:
        this.dialogHeader = 'Confirm remove user'
        this.dialogText = "Are you sure you want to remove this user from the group?"
        this.confirmBtnText = 'Remove user';
        this.backgroundColor = '#f95959';
        break;
      case ConfirmationDialogMode.RemoveUsers:
        this.dialogHeader = 'Confirm remove users'
        this.dialogText = "Are you sure you want to remove these users from the group?"
        this.confirmBtnText = 'Remove users';
        this.backgroundColor = '#f95959';
        break;
      case ConfirmationDialogMode.DeleteGroup:
        this.dialogHeader = 'Confirm delete group'
        this.dialogText = "Are you sure you want to delete this group? This action cannot be undone."
        this.confirmBtnText = 'Delete group';
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
      case ConfirmationDialogMode.DeleteAccount:
        this.dialogHeader = "Confirm Delete Account";
        this.dialogText = "Are you sure you want to delete your Account?"
        this.confirmBtnText = 'Delete Account';
        this.backgroundColor = '#f95959';
        break;
      default:
        console.error("Invalid confirmation dialog mode");
        break;
    }
  }
}
