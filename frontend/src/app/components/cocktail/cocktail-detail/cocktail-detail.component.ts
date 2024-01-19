import {Component, Inject, Optional} from '@angular/core';
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

  cocktailId: number = null;
  isModal: boolean = false;
  shared: boolean = false;

  constructor(
    @Optional() @Inject(MAT_DIALOG_DATA) public props: { cocktailId: number|null },
    private cocktailService: CocktailService,
    private notification: ToastrService,
    private errorFormatter: ErrorFormatterService,
    private route: ActivatedRoute,
    private location: Location,
  ) {
  }

  ngOnInit(): void {
    if (this.props != null && this.props.cocktailId != null) {
      this.cocktailId = this.props.cocktailId;
      this.isModal = true;
    } else {
      this.cocktailId = this.route.snapshot.params['id'];
    }
    this.getCocktail(this.cocktailId);
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

  /**
   * Share the current cocktail by copying its URL to the clipboard.
   */
  shareCocktail() {
    const url = window.location.host + '/#/cocktail/' + this.cocktail.id + '/detail';
    navigator.clipboard.writeText(url).then(() => {
      this.shared = true;
      this.notification.success(`Successfully copied URL to clipboard.`);
    }, () => {
      this.notification.error(`Error copying URL to clipboard.`);
    });
  }
}
