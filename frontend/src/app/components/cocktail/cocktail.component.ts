import { Component } from '@angular/core';
import {debounceTime, Subject} from 'rxjs';
import {CocktailService} from 'src/app/services/cocktail.service';
import {CocktailListDto} from '../../dtos/cocktail';
import {ToastrService} from 'ngx-toastr';

@Component({
  selector: 'app-cocktail',
  templateUrl: './cocktail.component.html',
  styleUrls: ['./cocktail.component.scss']
})
export class CocktailComponent {

  cocktails: CocktailListDto[] = [];
  searchChangedObservable = new Subject<void>();
  nameOfCocktail: String;
  nameOfIngredient: String;
  bannerError: string | null = null;
  firstImageUrlPart:string = "https://www.thecocktaildb.com/images/ingredients/"; 
  imageUrl: string = "";
  isToShowImg: boolean = false;
  imageName: String = "";
  selectedCocktail: String = ""

  constructor(
    private service: CocktailService,
    private notification: ToastrService,
  ) { }

  
  searchChanged() {
    if(this.nameOfCocktail.length != 0 || this.nameOfIngredient.length != 0){
      this.isToShowImg = false;
      if(!this.nameOfIngredient){
        this.service.searchByCoctailName(this.nameOfCocktail)
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
            this.notification.error(errorMessage, 'Could Not Fetch Cocktails');
          }
        });
      } else if(!this.nameOfCocktail){
        this.service.searchByIngredientName(this.nameOfIngredient)
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
            this.notification.error(errorMessage, 'Could Not Fetch Cocktails');
          }
        });
      } else {
        this.service.search(this.nameOfCocktail, this.nameOfIngredient)
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
            this.notification.error(errorMessage, 'Could Not Fetch Cocktails');
          }
        });
      }
    } else {
      this.cocktails = []
      this.isToShowImg = false;
      this.selectedCocktail = ""
    }

  }
  
  showImage(name: String) : void {
    this.isToShowImg = true;
    this.imageUrl = this.firstImageUrlPart + name + "-Medium.png"; 
    this.imageName = name;
    this.selectedCocktail = name;
  }
}