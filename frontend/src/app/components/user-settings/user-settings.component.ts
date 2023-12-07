import { Component } from '@angular/core';
import {AuthService} from "../../services/auth.service";
import {Router} from "@angular/router";
import {UserService} from "../../services/user.service";
import {DialogService} from "../../services/dialog.service";
import {ConfirmationDialogMode} from "../../confirmation-dialog/confirmation-dialog.component";
import {UserListDto} from "../../dtos/user";
import {IngredientListDto} from "../../dtos/ingredient";
import {Observable, of} from "rxjs";
import {IngredientService} from "../../services/ingredient.service";
import {ToastrService} from "ngx-toastr";
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
  currentMode: Modes = Modes.Ingredients; // Initializing to AccountSettings by default
  protected readonly Modes = Modes;


  constructor(
    private authService: AuthService,
    private userService: UserService,
    private ingredientService: IngredientService,
    private router: Router,
    private dialogService: DialogService,
    private notification: ToastrService,
  ) {
  }

  protected readonly username = JSON.parse(localStorage.getItem('user')).name;
  protected readonly email = JSON.parse(localStorage.getItem('user')).email;
  protected readonly userId = JSON.parse(localStorage.getItem('user')).id;

  ingredient: IngredientListDto = {
    id: null,
    name: ''
  };

  userIngredients: IngredientListDto[] = [];

  ingredientSuggestions = (input: string): Observable<UserListDto[]> => (input === '')
    ? of([])
    : this.ingredientService.searchIngredientsUserExisting(input, this.userId);

  public formatIngredient(ingredient: IngredientListDto | null): string {
    return ingredient?.name ?? '';
  }

  ngOnInit(): void {
    this.getUserIngredients();
  }

  addIngredient(ingredient: IngredientListDto): void {
    if (ingredient == null || ingredient.id == null)
      return;

    for (let i = 0; i < this.userIngredients.length; i++) {
      if (this.userIngredients[i].id === ingredient.id) {
        this.notification.error(`Ingredient "${ingredient.name}" is already in your list.`);
        return;
      }

    }
    this.userIngredients.push(ingredient);
    this.ingredient = null;
  }

  removeIngredient(index: number) {
    this.userIngredients.splice(index, 1);
  }

  saveIngredients() {
    this.ingredientService.saveUserIngredients(this.userIngredients).subscribe({
      next: () => {
        this.notification.success('Ingredients saved successfully.');
      },
      error: error => {
        this.notification.error('Could not save ingredients.');
        console.log('Could not save ingredients due to:');
        console.log(error);
      }
    });
  }



  getUserIngredients(): void {
    this.ingredientService.getUserIngredients().subscribe({
      next: ingredients => {
        this.userIngredients = ingredients;
      },
      error: error => {
        console.log('Could not load ingredients due to:');
        console.log(error);
      }
    });
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
