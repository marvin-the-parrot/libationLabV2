import {Component, OnInit } from '@angular/core';
import {debounceTime, Subject} from 'rxjs';
import {IngredientService} from 'src/app/services/ingredient.service';
import {IngredientListDto} from '../../dtos/ingredient';
import {ToastrService} from 'ngx-toastr';
import { Router } from '@angular/router';

@Component({
  selector: 'app-ingredient',
  templateUrl: './ingredient.component.html',
  styleUrls: ['./ingredient.component.scss']
})
export class IngredientComponent implements OnInit {
  ingredients: IngredientListDto[] = [];
  searchChangedObservable = new Subject<void>();
  nameOfIngredient: String;
  bannerError: string | null = null;
  firstImageUrlPart:string = "https://www.thecocktaildb.com/images/ingredients/";
  imageUrl: string = "";
  isToShowImg: boolean = false;
  imageName: String = "";
  selectedIngredient: String = ""

  constructor(
    private service: IngredientService,
    private notification: ToastrService,
    private router: Router,
  ) { }

  ngOnInit(): void {
  }

  searchChanged() {
    if(this.nameOfIngredient.length != 0){
      this.isToShowImg = false;
      this.selectedIngredient = null;
      this.service.search(this.nameOfIngredient)
      .subscribe({
        next: data => {
          this.ingredients = data;
          if (data == null) {
            this.isToShowImg = false;
          }
        },
        error: error => {
          console.error('Error fetching ingredients', error);
          this.bannerError = 'Could not fetch ingredients: ' + error.message;
          const errorMessage = error.status === 0
            ? 'Is the backend up?'
            : error.message.message;
          this.notification.error(errorMessage, 'Could Not Fetch Ingredients');
        }
      });
    } else {
      this.ingredients = []
      this.isToShowImg = false;
      this.selectedIngredient = null;
    }

  }
  
  showImage(name: String) : void {
    this.isToShowImg = true;
    this.imageUrl = this.firstImageUrlPart + name + "-Medium.png"; 
    this.imageName = name;
    this.selectedIngredient = name;
  }

  navigateToMyIngredients(): void {
    this.router.navigate(['/settings']);
  }
  
}
