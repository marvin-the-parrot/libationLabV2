import { Component } from '@angular/core';
import {AuthService} from "../../services/auth.service";
import {Router} from "@angular/router";

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
    private router: Router
  ) {
  }
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



  logout() {
    console.log('Logging out...');
    this.authService.logoutUser();
    this.router.navigate(['/login']);
  }
}
