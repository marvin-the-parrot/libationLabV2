import { Component } from '@angular/core';
import {debounceTime, Subject} from 'rxjs';
import {CocktailService} from 'src/app/services/cocktail.service';
import {CocktailListDto, CocktailSearch} from '../../dtos/cocktail';
import {ToastrService} from 'ngx-toastr';
import { List } from 'lodash';

@Component({
  selector: 'app-cocktail',
  templateUrl: './cocktail.component.html',
  styleUrls: ['./cocktail.component.scss']
})
export class CocktailComponent {

  cocktails: CocktailListDto[] = [];
  cocktailIngredients: List<String>;
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
    private service: CocktailService,
    private notification: ToastrService,
  ) { }


  searchChanged() {
    if((this.nameOfCocktail && this.nameOfCocktail.length != 0) || (this.nameOfIngredient && this.nameOfIngredient.length != 0) || (this.nameOfPreference && this.nameOfPreference.length != 0)){
      this.isToShowImg = false;
      this.selectedCocktail = "";
      this.searchParams.cocktailName = this.nameOfCocktail;
      this.searchParams.ingredientName = this.nameOfIngredient;
      this.searchParams.preferenceName = this.nameOfPreference;
      this.service.searchCocktails(this.searchParams)
      .subscribe({
        next: data => {
          this.cocktails = data;
          if (data == null) {
            this.isToShowImg = false;
          }
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
      this.cocktails = [];
      this.isToShowImg = false;
      this.selectedCocktail = "";
    }

  }

  showImage(name: String) : void {
    this.isToShowImg = true;
    this.imageUrl = this.getCocktailImageByName(name).imagePath;
    this.cocktailIngredients = this.getCocktailImageByName(name).ingredientsName;
    console.log(this.imageUrl)
    this.imageName = name;
    this.selectedCocktail = name;
  }

  getCocktailImageByName(cocktailName: String): CocktailListDto | undefined {
    return this.cocktails.find((cocktail) => cocktail.name === cocktailName);
  }

}
