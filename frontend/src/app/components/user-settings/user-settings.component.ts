import { Component } from '@angular/core';
import {AuthService} from "../../services/auth.service";
import {ActivatedRoute, Router} from "@angular/router";
import {UserService} from "../../services/user.service";
import {DialogService} from "../../services/dialog.service";
import {ConfirmationDialogMode} from "../../confirmation-dialog/confirmation-dialog.component";
import {IngredientListDto} from "../../dtos/ingredient";
import {Observable, of} from "rxjs";
import {IngredientService} from "../../services/ingredient.service";
import {PreferenceService} from "../../services/preference.service";
import {ToastrService} from "ngx-toastr";
import {PreferenceListDto} from "../../dtos/preference";
import {CocktailService} from "../../services/cocktail.service";
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
    private ingredientService: IngredientService,
    private preferenceService: PreferenceService,
    private cocktailService: CocktailService,
    private router: Router,
    private dialogService: DialogService,
    private notification: ToastrService,
    private route: ActivatedRoute
  ) {
  }

  protected readonly username = JSON.parse(localStorage.getItem('user')).name;
  protected readonly email = JSON.parse(localStorage.getItem('user')).email;

  ingredient: IngredientListDto = {
    id: null,
    name: ''
  };

  preference: PreferenceListDto = {
    id: null,
    name: ''
  };

  userIngredients: IngredientListDto[] = [];
  userPreferences: PreferenceListDto[] = [];

  ingredientSuggestions = (input: string): Observable<IngredientListDto[]> => (input === '')
    ? of([])
    : this.ingredientService.searchIngredientsUserExisting(input);

  preferenceSuggestion = (input: string): Observable<PreferenceListDto[]> => (input === '')
    ? of([])
    : this.preferenceService.searchPreferencesUserExisting(input);

  public formatIngredient(ingredient: IngredientListDto | null): string {
    return ingredient?.name ?? '';
  }

  public formatPreference(preference: PreferenceListDto | null): string {
    return preference?.name ?? '';
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      if (params['mode']) {
        this.currentMode = params['mode'];
      }
    });
    this.getUserIngredients();
    this.getUserPreferences();
  }

  addIngredient(ingredient: IngredientListDto): void {
    if (ingredient == null || ingredient.id == null)
      return;

    for (let i = 0; i < this.userIngredients.length; i++) {
      if (this.userIngredients[i].id === ingredient.id) {
        this.notification.error(`Ingredient "${ingredient.name}" is already in your list.`);
        this.ingredient = null;
        return;
      }

    }
    this.userIngredients.push(ingredient);
    this.ingredient = null;
  }

  addPreference(preference: PreferenceListDto): void {
    if (preference == null || preference.id == null)
      return;

    for (let i = 0; i < this.userPreferences.length; i++) {
      if (this.userPreferences[i].id === preference.id) {
        this.notification.error(`Preference "${preference.name}" is already in your list.`);
        this.preference = null;
        return;
      }

    }
    this.userPreferences.push(preference);
    this.preference = null;
  }

  removeIngredient(index: number) {
    this.userIngredients.splice(index, 1);
  }

  removePreference(index: number) {
    this.userPreferences.splice(index, 1);
  }

  saveIngredients() {
    this.ingredientService.saveUserIngredients(this.userIngredients).subscribe({
      next: () => {
        this.notification.success('Ingredients saved successfully.');
        this.updateMixableCocktails();
      },
      error: error => {
        console.log('Could not save ingredients due to:', error);
        this.notification.error(error.error.detail, 'Could not save ingredients:', {
          enableHtml: true,
          timeOut: 10000,
        });
      }
    });
  }

  updateMixableCocktails() {
    this.cocktailService.updateCocktailMenu(this.userIngredients).subscribe({
      next: () => {
        this.notification.success('Mixable Cocktails in all groups updated successfully.');
      },
      error: error => {
        console.log('Could not update cocktails due to:', error);
        this.notification.error(error.error.detail, 'Could not update cocktails.', {
          enableHtml: true,
          timeOut: 10000,
        });
      }
    });
  }

  savePreference() {
    this.preferenceService.saveUserPreferences(this.userPreferences).subscribe({
      next: () => {
        this.notification.success('Preferences saved successfully.');
      },
      error: error => {
        this.notification.error('Could not save preferences.');
        console.log('Could not save preferences due to:');
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

  getUserPreferences(): void {
    this.preferenceService.getUserPreferences().subscribe({
      next: preferences => {
        this.userPreferences = preferences;
      },
      error: error => {
        console.log('Could not load preferences due to:');
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
