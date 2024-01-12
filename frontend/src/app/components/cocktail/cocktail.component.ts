import {Component} from '@angular/core';
import {Observable, of, Subject} from 'rxjs';
import {CocktailService} from 'src/app/services/cocktail.service';
import {CocktailListDto, CocktailSearch} from '../../dtos/cocktail';
import {ToastrService} from 'ngx-toastr';
import {List} from 'immutable'; // Import List from Immutable.js
import {IngredientListDto} from "../../dtos/ingredient";
import {PreferenceListDto} from "../../dtos/preference";

@Component({
  selector: 'app-cocktail',
  templateUrl: './cocktail.component.html',
  styleUrls: ['./cocktail.component.scss']
})
export class CocktailComponent {

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
  searchParams: CocktailSearch = {};

  constructor(
    private cocktailService: CocktailService
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
      this.isToShowImg = false;
      this.selectedCocktail = "";
      this.searchParams.cocktailName = this.nameOfCocktail;
      this.searchParams.ingredientsName = this.nameOfIngredient;
      this.searchParams.preferenceName = this.nameOfPreference;
      this.searchCocktails();
    } else {
      this.searchParams.cocktailName = "";
      this.searchParams.ingredientsName = "";
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
    console.log(this.cocktailIngredients);
    console.log(this.imageUrl);
    this.imageName = name;
    this.selectedCocktail = name;
  }

  getCocktailImageByName(cocktailName: String): CocktailListDto | undefined {
    return this.cocktails.find((cocktail) => cocktail.name === cocktailName);
  }

}
