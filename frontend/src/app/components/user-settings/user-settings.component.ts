import { Component } from '@angular/core';
import {AuthService} from "../../services/auth.service";
import {Router} from "@angular/router";
import {UserService} from "../../services/user.service";
import {DialogService} from "../../services/dialog.service";
import {ConfirmationDialogMode} from "../../confirmation-dialog/confirmation-dialog.component";

export enum Modes {
  AccountSettings = 'AccountSettings',
  Ingredients = 'Ingredients',
  Preferences = 'Preferences'
}


@Component({
  selector: 'app-user-settings',
  templateUrl: './user-settings.component.html',
  styleUrls: ['./user-settings.component.scss']
})
export class UserSettingsComponent {
  currentMode: Modes = Modes.AccountSettings; // Initializing to AccountSettings by default
  protected readonly Modes = Modes;


  constructor(
    private authService: AuthService,
    private userService: UserService,
    private router: Router,
    private dialogService: DialogService,
  ) {
  }

  protected readonly username = JSON.parse(localStorage.getItem('user')).name;
  protected readonly email = JSON.parse(localStorage.getItem('user')).email;

  setMode(mode: string): void {
    // Perform actions based on the selected mode
    if (mode === 'AccountSettings') {
      this.currentMode = Modes.AccountSettings;
    } else if (mode === 'Ingredients') {
      this.currentMode = Modes.Ingredients;
    } else if (mode === 'Preferences') {
      this.currentMode = Modes.Preferences;
    }
    console.log('Mode changed to: ' + this.currentMode);
  }

  deleteAccount() {
    this.dialogService.openConfirmationDialog(ConfirmationDialogMode.DeleteAccount).subscribe((result) => {
      if (result) {
        console.log('Deleting account...');
        this.userService.deleteUser().subscribe({
          next: () => {
            console.log('Account deleted');
            localStorage.clear();
          },
          error: error => {
            console.log('Could not delete account due to:');
            console.log(error);
          }
        });
        this.router.navigate(['/login']);
      }
    });
  }



  logout() {
    console.log('Logging out...');
    this.authService.logoutUser();
    this.router.navigate(['/login']);
  }
}
