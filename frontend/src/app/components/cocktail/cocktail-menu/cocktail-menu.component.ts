import {Component} from '@angular/core';
import {IngredientGroupDto, IngredientListDto} from "../../../dtos/ingredient";
import {GroupsService} from "../../../services/groups.service";
import {UserService} from "../../../services/user.service";
import {IngredientService} from "../../../services/ingredient.service";
import {DialogService} from "../../../services/dialog.service";
import {CocktailService} from 'src/app/services/cocktail.service';
import {MessageService} from "../../../services/message.service";
import {ToastrService} from "ngx-toastr";
import {ActivatedRoute, Router} from "@angular/router";
import {CocktailListDto, CocktailOverviewDto, CocktailSearch} from "../../../dtos/cocktail";
import {ErrorFormatterService} from "../../../services/error-formatter.service";
import {MenuCocktailsDto} from 'src/app/dtos/menu';
import {PreferenceListDto} from "../../../dtos/preference";
import {Observable, of} from "rxjs";
import {List} from 'immutable';

@Component({
  selector: 'app-cocktail-card',
  templateUrl: './cocktail-menu.component.html',
  styleUrls: ['./cocktail-menu.component.scss']
})
export class CocktailMenuComponent {
  cocktails: CocktailOverviewDto[] = []
  ingredients: IngredientGroupDto[] = [];
  groupId: number;
  numberOfCocktails = 4;
  lv: number;
  dummyMemberSelectionModel: unknown; // Just needed for the autocomplete
  submitted = false;
  // Error flag
  error = false;
  errorMessage = '';
  cocktails_list: CocktailListDto[] = [];
  nameOfCocktail: string;
  nameOfIngredient: string;
  nameOfPreference: string;
  bannerError: string | null = null;
  selectedCocktails: CocktailListDto[] = [];
  searchParams: CocktailSearch = {};

  constructor(
    private groupsService: GroupsService,
    private userService: UserService,
    private ingredientService: IngredientService,
    private dialogService: DialogService,
    private messageService: MessageService,
    private notification: ToastrService,
    private route: ActivatedRoute,
    private router: Router,
    private errorFormatter: ErrorFormatterService,
    private cocktailService: CocktailService,
  ) {
  }

  ingredient: IngredientListDto = {
    id: null,
    name: ''
  };

  preference: PreferenceListDto = {
    id: null,
    name: ''
  };

  ingredientSuggestions = (input: string): Observable<IngredientListDto[]> => (input === '')
    ? of([])
    : this.cocktailService.searchIngredientsAuto(input);

  preferenceSuggestion = (input: string): Observable<PreferenceListDto[]> => (input === '')
    ? of([])
    : this.cocktailService.searchPreferencesAuto(input);

  public formatIngredient(ingredient: IngredientListDto | null): string {
    return ingredient?.name ?? '';
  }

  public formatPreference(preference: PreferenceListDto | null): string {
    return preference?.name ?? '';
  }

  ngOnInit(): void {
    this.groupId = this.route.snapshot.params['id'];
    this.getGroup();
    this.getCocktailsMenu(this.groupId);
  }

  /**
   * Get group data by the id from the route. Used to initially get the group and refresh it after a change.
   */
  private getGroup() {
    this.groupsService.getMixables(this.groupId).subscribe({
      next: (cocktails: CocktailOverviewDto[]) => {
        this.cocktails = cocktails;
      },
      error: error => {
        console.error('Could not fetch cocktails due to:');
        this.defaultServiceErrorHandling(error);
        // todo: Handle error appropriately (e.g., show a message to the user)
      }
    });
  }

  private defaultServiceErrorHandling(error: any) {
    console.log(error);
    this.error = true;
    this.notification.error(error.error.detail);
  }

  /**
   * Opens the ingredient suggestion dialog to let the user add a new ingredient
   */
  openAddIngredientModal() {
    this.dialogService.openAddIngredientDialog(this.groupId).subscribe((result: boolean) => {
      console.log("Added new ingredient: " + result); // true if added, false if error while adding, undefined if the user just closed the modal
      if (result === true) {
        this.getGroup();
      }
    });
  }


  searchChanged() {
    if (this.ingredient != null) {
      this.nameOfIngredient = this.ingredient.name;
    } else {
      this.nameOfIngredient = "";
    }
    if (this.preference != null) {
      this.nameOfPreference = this.preference.name;
    } else {
      this.nameOfPreference = "";
    }
    if ((this.nameOfCocktail && this.nameOfCocktail.length != 0) || (this.nameOfIngredient && this.nameOfIngredient.length != 0) || (this.nameOfPreference && this.nameOfPreference.length != 0)) {
      this.searchParams.cocktailName = this.nameOfCocktail;
      this.searchParams.ingredientsName = this.nameOfIngredient;
      this.searchParams.preferenceName = this.nameOfPreference;
      this.cocktailService.searchCocktails(this.searchParams)
        .subscribe({
          next: data => {
            this.cocktails_list = data;
          },
          error: error => {
            console.error('Error fetching cocktails', error);
            this.bannerError = 'Could not fetch cocktails: ' + error.message;
            const errorMessage = error.status === 0
              ? 'Is the backend up?'
              : error.message.message;
          }
        });
    } else {
      this.searchParams.cocktailName = "";
      this.searchParams.ingredientsName = "";
      this.searchParams.preferenceName = "";
      this.cocktailService.searchCocktails(this.searchParams)
        .subscribe({
          next: data => {
            this.cocktails_list = data;
          },
          error: error => {
            console.error('Error fetching cocktails', error);
            this.bannerError = 'Could not fetch cocktails: ' + error.message;
            const errorMessage = error.status === 0
              ? 'Is the backend up?'
              : error.message.message;
          }
        });
    }

  }

  clickOnCocktailImage(cocktailName: string): void {
    const isSelected = this.isSelected(cocktailName);

    if (isSelected) {
      // The cocktail is already selected, remove it from the list
      const index = this.selectedCocktails.findIndex(cocktail => cocktail.name === cocktailName);
      if (index !== -1) {
        this.selectedCocktails.splice(index, 1);
      }
    } else {
      // The cocktail was not selected, add it to the list
      const selectedCocktail = this.cocktails_list.find(cocktail => cocktail.name === cocktailName);
      if (selectedCocktail) {
        this.selectedCocktails.push(selectedCocktail);
      }
    }
  }

  isSelected(cocktailName: string): boolean {
    return this.selectedCocktails.some(cocktail => cocktail.name === cocktailName);
  }

  removeCocktail(index: number) {
    this.selectedCocktails.splice(index, 1);
  }

  saveCocktails() {
    const newMenuCocktails: MenuCocktailsDto = {
      groupId: this.groupId,
      cocktailsList: this.selectedCocktails
    };
    this.cocktailService.saveCocktails(newMenuCocktails).subscribe({
      next: () => {
        this.notification.success('Cocktail Card saved successfully.');
        this.router.navigate(['/groups/' + newMenuCocktails.groupId + '/detail']);
      },
      error: error => {
        this.notification.error('Could not save cocktail card.');
        console.log('Could not save cocktail card due to:');
        console.log(error);
      }
    });
  }

  private getCocktailsMenu(groupId: number): void {
    this.cocktailService.getCocktailMenu(groupId).subscribe({
      next: (menu: MenuCocktailsDto) => {
        this.selectedCocktails = menu.cocktailsList;
      },
      error: error => {
        console.error('Could not fetch cocktails menu due to:');
      }
    });
  }

  getIngredientsString(ingredients: List<string>): string {
    if (!ingredients || ingredients.size === 0) {
      return 'No ingredients listed';
    }

    const ingredientNames = ingredients.join(', ');
    return `Ingredients: ${ingredientNames}`;
  }

  openSelectMenuModal() {
    this.dialogService.openGenerateMenuDialog(this.groupId, this.numberOfCocktails).subscribe((result: boolean) => {
      if (result === true) {
        this.getCocktailsMenu(this.groupId);
      }
    });
  }
}


