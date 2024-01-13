import {Component, ViewChild} from '@angular/core';
import {Observable, of, Subject} from 'rxjs';
import {CocktailService} from 'src/app/services/cocktail.service';
import {CocktailListDto, CocktailSearch, CocktailTagSearchDto} from '../../dtos/cocktail';
import {ToastrService} from 'ngx-toastr';
import {List} from 'immutable'; // Import List from Immutable.js
import {IngredientListDto} from "../../dtos/ingredient";
import {PreferenceListDto} from "../../dtos/preference";
import { AutocompleteComponent } from '../autocomplete/autocomplete.component';



@Component({
  selector: 'app-cocktail',
  templateUrl: './cocktail.component.html',
  styleUrls: ['./cocktail.component.scss']
})
export class CocktailComponent {

  @ViewChild(AutocompleteComponent)
  private autocompleteComponent!: AutocompleteComponent<any>;

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
  searchParams: CocktailTagSearchDto = {};

  constructor(
    private cocktailService: CocktailService,
    private notification: ToastrService,

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
        this.autocompleteComponent.resetAutocompleteInput(); // Reset autocomplete input
        this.ingredient.name = "";
        return;
      }
      this.selectedIngredients.push(this.ingredient.name);
      this.autocompleteComponent.resetAutocompleteInput(); // Reset autocomplete input
      this.ingredient.name = "";

    } else {
      this.nameOfIngredient = "";
    }
    if (this.preference != null) {
      this.nameOfPreference = this.preference.name;
    } else {
      this.nameOfPreference = "";
    }
    if ((this.nameOfCocktail && this.nameOfCocktail.length != 0) || (this.selectedIngredients && this.selectedIngredients.length != 0) || (this.nameOfPreference && this.nameOfPreference.length != 0)) {
      this.isToShowImg = false;
      this.selectedCocktail = "";
      this.searchParams.cocktailName = this.nameOfCocktail;
      this.searchParams.selectedIngredients = this.selectedIngredients;
      this.searchParams.preferenceName = this.nameOfPreference;
      console.log(this.searchParams);
      this.searchCocktails();
    } else {
      this.searchParams.cocktailName = "";
      this.searchParams.selectedIngredients = [];
      this.searchParams.preferenceName = "";
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


  removeTag(tag: string): void {
    const index = this.selectedIngredients.indexOf(tag);
    if (index !== -1) {
      this.selectedIngredients.splice(index, 1);
    }
    this.searchChanged();
  }
}
