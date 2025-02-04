import {Component, ViewChild} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {ToastrService} from "ngx-toastr";
import {NgForm, NgModel} from "@angular/forms";
import {Observable, of} from "rxjs";
import {CocktailCreateDto, CocktailDetailDto} from "../../../dtos/cocktail";
import {CocktailService} from "../../../services/cocktail.service";
import {IngredientService} from "../../../services/ingredient.service";
import {IngredientListDto} from "../../../dtos/ingredient";
import {PreferenceListDto} from "../../../dtos/preference";
import {AutocompleteComponent} from "../../autocomplete/autocomplete.component";

export enum CocktailCreateEditMode {
  create,
  edit,
}
@Component({
  selector: 'app-cocktail-create',
  templateUrl: './cocktail-create.component.html',
  styleUrls: ['./cocktail-create.component.scss']
})
export class CocktailCreateComponent {
  mode: CocktailCreateEditMode = CocktailCreateEditMode.create;
  cocktail: CocktailCreateDto = {
    name: '',
    imagePath: '',
    ingredients: null,
    preferenceName: null,
    instructions: '',
  };
  selectedIngredients: string[] = []; // List of selected ingredients (tags)

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
    private ingredientService: IngredientService,
    private router: Router,
    private route: ActivatedRoute,
    private notification: ToastrService,
  ) {
  }

  public get submitButtonText(): string {
    switch (this.mode) {
      case CocktailCreateEditMode.create:
        return 'Create';
      case CocktailCreateEditMode.edit:
        return 'Update';
      default:
        return '?';
    }
  }

  get modeIsCreate(): boolean {
    return this.mode === CocktailCreateEditMode.create;
  }

  private get modeActionFinished(): string {
    switch (this.mode) {
      case CocktailCreateEditMode.create:
        return 'created';
      case CocktailCreateEditMode.edit:
        return 'updated';
      default:
        return '?';
    }
  }

  ngOnInit(): void {
    this.route.data.subscribe(data => {
      this.mode = data.mode;
    });
    this.route.params.subscribe(params =>{
      this.cocktail.id  = params['id'];
    })
    this.loadHorse()
  }

  public dynamicCssClassesForInput(input: NgModel): any {
    return {
      'is-invalid': !input.valid && !input.pristine,
    };
  }

  loadHorse() {
    if (this.cocktail.id != undefined){
      this.cocktailService.getCocktailById(this.cocktail.id)
        .subscribe({
          next: data => {
            this.cocktail = data
          },
          error: error => {
            console.error('Error fetching cocktail', error);
          }
        });
    }
  }
  public onSubmit(form: NgForm): void {
    console.log('is form valid?', form.valid, this.cocktail);
    if (form.valid) {
      let observable: Observable<CocktailDetailDto>;
      switch (this.mode) {
        case CocktailCreateEditMode.create:
          observable = this.cocktailService.create(this.cocktail);
          break;
        case CocktailCreateEditMode.edit:
          observable = this.cocktailService.update(this.cocktail);
          break;
        default:
          console.error('Unknown CocktailCreateEditMode', this.mode);
          return;
      }
      observable.subscribe({
        next: data => {
          this.notification.success(`Cocktail ${this.cocktail.name} successfully ${this.modeActionFinished}.`);

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

      if (this.selectedIngredients.includes(this.ingredient.name)) {
        this.notification.info("You already added this ingredient!");
        this.ingredientAutocompleteComponent.resetAutocompleteInput(); // Reset autocomplete input
        this.ingredient.name = "";
        return;
      }
      this.selectedIngredients.push(this.ingredient.name);
      this.ingredientAutocompleteComponent.resetAutocompleteInput(); // Reset autocomplete input
      this.ingredient.name = "";
    }

  }

  removeIngredient(i: number) {
    this.selectedIngredients.splice(i, 1);
  }
}
