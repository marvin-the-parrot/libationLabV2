import {Component, ViewChild} from '@angular/core';
import {Observable, of, Subject} from 'rxjs';
import {CocktailService} from 'src/app/services/cocktail.service';
import {CocktailListDto, CocktailTagSearchDto} from '../../dtos/cocktail';
import {ToastrService} from 'ngx-toastr';
import {List} from 'immutable'; // Import List from Immutable.js
import {IngredientListDto} from "../../dtos/ingredient";
import {PreferenceListDto} from "../../dtos/preference";
import {AutocompleteComponent} from '../autocomplete/autocomplete.component';
import {DialogService} from "../../services/dialog.service";


@Component({
  selector: 'app-cocktail',
  templateUrl: './cocktail.component.html',
  styleUrls: ['./cocktail.component.scss']
})
export class CocktailComponent {

  @ViewChild('ingredientAutocomplete', {static: false})
  private ingredientAutocompleteComponent!: AutocompleteComponent<any>;

  @ViewChild('preferenceAutocomplete', {static: false})
  private preferenceAutocompleteComponent!: AutocompleteComponent<any>;

  cocktails: CocktailListDto[] = [];
  cocktailIngredients: Map<string, string>;
  userPreferences: List<String>;
  searchChangedObservable = new Subject<void>();
  nameOfCocktail: string;
  nameOfIngredient: string;
  nameOfPreference: string;
  bannerError: string | null = null;
  imageUrl: string = "";
  isToShowImg: boolean = false;
  imageName: String = "";
  selectedCocktail: String = ""
  selectedIngredients: string[] = []; // List of selected ingredients (tags)
  selectedPreferences: string[] = []; // List of selected preferences (tags)
  searchParams: CocktailTagSearchDto = {};

  constructor(
    private cocktailService: CocktailService,
    private notification: ToastrService,
    private dialogService: DialogService,
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
    this.searchChanged();
  }

  /**
   * This method is called when the page is loaded or when the user changes the search parameters.
   */
  searchChanged() {
    console.log("searchChanged")
    if (this.ingredient != null && this.ingredient.name !== "") {

      if (this.selectedIngredients.includes(this.ingredient.name)) {
        this.notification.info("You already added this ingredient!");
        this.ingredientAutocompleteComponent.resetAutocompleteInput(); // Reset autocomplete input
        this.ingredient.name = "";
        return;
      }
      this.selectedIngredients.push(this.ingredient.name);
      this.ingredientAutocompleteComponent.resetAutocompleteInput(); // Reset autocomplete input
      this.ingredient.name = "";

    } else {
      this.nameOfIngredient = "";
    }
    if (this.preference != null && this.preference.name !== "") {
      if (this.selectedPreferences.includes(this.preference.name)) {
        this.notification.info("You already added this preference!");
        this.preferenceAutocompleteComponent.resetAutocompleteInput(); // Reset autocomplete input
        this.preference.name = "";
        return;
      }
      this.selectedPreferences.push(this.preference.name);
      this.preferenceAutocompleteComponent.resetAutocompleteInput(); // Reset autocomplete input
      this.preference.name = "";
    } else {
      this.nameOfPreference = "";
    }
    if ((this.nameOfCocktail && this.nameOfCocktail.length != 0) || (this.selectedIngredients && this.selectedIngredients.length != 0) || (this.selectedPreferences && this.selectedPreferences.length != 0)) {
      this.isToShowImg = false;
      this.selectedCocktail = "";
      this.searchParams.cocktailName = this.nameOfCocktail;
      this.searchParams.selectedIngredients = this.selectedIngredients;
      this.searchParams.selectedPreferences = this.selectedPreferences;
      console.log(this.searchParams);
      this.searchCocktails();
    } else {
      this.searchParams.cocktailName = "";
      this.searchParams.selectedIngredients = [];
      this.searchParams.selectedPreferences = [];
      this.searchCocktails();
      this.isToShowImg = false;
      this.selectedCocktail = "";
    }

  }

  /**
   * This method requests the cocktails from the backend, updates the cocktails array and shows the first cocktail.
   * It is called from the {@link searchChanged} method, when the user changes the search parameters.
   */
  private searchCocktails(): void {
    this.cocktailService.searchCocktails(this.searchParams)
      .subscribe({
        next: data => {
          this.cocktails = data;
          if (data == null) {
            this.isToShowImg = false;
          }
          this.showImage(this.cocktails[0].name);
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

  showImage(name: String): void {
    this.isToShowImg = true;
    this.imageUrl = this.getCocktailImageByName(name).imagePath;
    this.cocktailIngredients = this.getCocktailImageByName(name).ingredients;
    this.imageName = name;
    this.selectedCocktail = name;
  }

  getCocktailImageByName(cocktailName: String): CocktailListDto | undefined {
    return this.cocktails.find((cocktail) => cocktail.name === cocktailName);
  }


  /**
   * This method is called when the user clicks on a 'X' from tag in the ingredients list.
   * It removes the tag from the list of selected ingredients and calls the {@link searchChanged} method.
   * @param tag
   */
  removeTagIngredients(tag: string): void {
    const index = this.selectedIngredients.indexOf(tag);
    if (index !== -1) {
      this.selectedIngredients.splice(index, 1);
    }
    this.searchChanged();
  }

  /**
   * This method is called when the user clicks on a 'X' from tag in the preferences list.
   * It removes the tag from the list of selected preferences and calls the {@link searchChanged} method.
   * @param tag
   */
  removeTagPreferences(tag: string): void {
    const index = this.selectedPreferences.indexOf(tag);
    if (index !== -1) {
      this.selectedPreferences.splice(index, 1);
    }
    this.searchChanged();
  }

  /**
   * Opens the cocktails details in a modal.
   * @param id The id of the cocktail to open
   */
  openCocktailDetails(id: number) {
    this.dialogService.openCocktailDetailDialog(id).subscribe({
      next: () => {
        console.log("Successfully opened cocktail details");
      },
      error: error => {
        console.error('Could not open cocktail details due to:');
        this.notification.error(error.error.detail);
      }
    });
  }

}
