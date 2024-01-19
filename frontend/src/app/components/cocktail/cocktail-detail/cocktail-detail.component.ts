import {Component, Inject} from '@angular/core';
import {CocktailDetailDto} from "../../../dtos/cocktail";
import {CocktailService} from "../../../services/cocktail.service";
import {ToastrService} from "ngx-toastr";
import {ErrorFormatterService} from "../../../services/error-formatter.service";
import {ActivatedRoute} from "@angular/router";
import {Location} from "@angular/common";
import {MAT_DIALOG_DATA} from "@angular/material/dialog";

@Component({
  selector: 'app-cocktail-detail',
  templateUrl: './cocktail-detail.component.html',
  styleUrls: ['./cocktail-detail.component.scss']
})
export class CocktailDetailComponent {

  cocktail: CocktailDetailDto = {
    id: 0,
    name: 'Loading...',
    imagePath: '',
    instructions: '',
    ingredients: new Map(),
    preferenceName: [],
  }



  constructor(
    @Inject(MAT_DIALOG_DATA) public props: { cocktailId: number },
    private cocktailService: CocktailService,
    private notification: ToastrService,
    private errorFormatter: ErrorFormatterService,
    private route: ActivatedRoute,
    private location: Location,
  ) {
  }

  ngOnInit(): void {
    const cocktailId = this.route.snapshot.params['id'];
    this.getCocktail(this.props.cocktailId);
  }


  /**
   * Get the cocktail with the given id from the backend.
   *
   * @param id The id of the cocktail to get.
   */
  private getCocktail(id: number): void {
    this.cocktailService.getCocktailById(id)
      .subscribe({
        next: data => {
          data.instructions = data.instructions.replace(". ", '.\n');
          this.cocktail = data;
          console.log("cocktail", this.cocktail)
        },
        error: error => {
          console.error("Error getting cocktail data", error);
          this.notification.error(this.errorFormatter.format(error.detail), `Error getting cocktail with id: "${id}".`, {
            enableHtml: true,
            timeOut: 10000,
          });
          this.location.back(); // Go back to the previous page
        }
      });
  }
}
