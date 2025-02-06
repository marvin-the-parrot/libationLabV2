import {Component, ViewChild} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {ToastrService} from "ngx-toastr";
import {NgForm, NgModel} from "@angular/forms";
import {Observable, of} from "rxjs";
import {CocktailCreateDto, CocktailDetailDto} from "../../../dtos/cocktail";
import {CocktailService} from "../../../services/cocktail.service";
import {CreateIngredientDto, IngredientListDto} from "../../../dtos/ingredient";
import {PreferenceListDto} from "../../../dtos/preference";
import {AutocompleteComponent} from "../../autocomplete/autocomplete.component";

@Component({
  selector: 'app-cocktail-create',
  templateUrl: './cocktail-create.component.html',
  styleUrls: ['./cocktail-create.component.scss']
})
export class CocktailCreateComponent {
  selectedIngredientsMap: Map<string, CreateIngredientDto> = new Map<string, CreateIngredientDto>();

  cocktail: CocktailCreateDto = {
    id: 0,
    name: '',
    imagePath: '',
    ingredients: this.selectedIngredientsMap,
    preferenceName: null,
    instructions: '',
  };

  ingredient: IngredientListDto = {
    id: null,
    name: ''
  };

  preference: PreferenceListDto = {
    id: null,
    name: ''
  };

  @ViewChild('ingredientAutocomplete', {static: false})
  private ingredientAutocompleteComponent!: AutocompleteComponent<any>;

  @ViewChild('preferenceAutocomplete', {static: false})
  private preferenceAutocompleteComponent!: AutocompleteComponent<any>;


  constructor(
    private cocktailService: CocktailService,
    private route: ActivatedRoute,
    private notification: ToastrService,
  ) {
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.cocktail.id = params['id'];
    })
  }

  public dynamicCssClassesForInput(input: NgModel): any {
    return {
      'is-invalid': !input.valid && !input.pristine,
    };
  }

  public onSubmit(form: NgForm): void {
    console.log('is form valid?', form.valid, this.cocktail);
    if (form.valid) {
      let observable: Observable<CocktailDetailDto>;
      // add all ingredients names as preferences
      this.cocktail.preferenceName = Array.from(this.selectedIngredientsMap.keys());
      observable = this.cocktailService.create(this.cocktail);
      observable.subscribe({
        next: data => {
          this.notification.success(`Cocktail ${this.cocktail.name} successfully created.`);

        },
        error: error => {
          this.notification.error(`${error.error.message} Error: ${error.error.errors}`);
        }
      });
    }
  }

  ingredientSuggestions = (input: string): Observable<IngredientListDto[]> => (input === '')
    ? of([])
    : this.cocktailService.searchIngredientsAuto(input);

  public formatIngredient(ingredient: IngredientListDto | null): string {
    return ingredient?.name ?? '';
  }

  /**
   * This method is called when the page is loaded or when the user changes the search parameters.
   */
  addIngredient() {
    if (this.ingredient != null && this.ingredient.name !== "") {
      //add ingredients to map which looks like this name -> createIngredientObject, then set all the properties and use them as values for the ngModel in the form
      if (this.selectedIngredientsMap.has(this.ingredient.name)) {
        this.notification.info("You already added this ingredient!");
        this.ingredientAutocompleteComponent.resetAutocompleteInput(); // Reset autocomplete input
        this.ingredient.name = "";
        return;
      }
      //construct the ingredient object
      let createIngredient: CreateIngredientDto = {
        id: this.ingredient.id,
        name: this.ingredient.name,
        amount: 0,
        measure: "parts"
      }
      this.selectedIngredientsMap.set(this.ingredient.name, createIngredient);
      this.ingredientAutocompleteComponent.resetAutocompleteInput(); // Reset autocomplete input
      this.ingredient.name = "";
    }

  }

  removeIngredient(key: string) {
    this.selectedIngredientsMap.delete(key);
  }
}
