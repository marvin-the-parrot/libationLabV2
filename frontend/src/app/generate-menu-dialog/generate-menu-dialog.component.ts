import {Component, EventEmitter, Inject, OnInit, Output} from '@angular/core';
import {MAT_DIALOG_DATA} from "@angular/material/dialog";
import {IngredientListDto, IngredientSuggestionDto} from "../dtos/ingredient";
import {ToastrService} from "ngx-toastr";
import {CocktailService} from 'src/app/services/cocktail.service';
import {ErrorFormatterService} from "../services/error-formatter.service";
import {RecommendedMenues} from "../dtos/recommendedMenues";
import {CocktailListDto} from "../dtos/cocktail";
import {MenuCocktailsDto} from "../dtos/menu"; // Import List from Immutable.js


@Component({
  selector: 'app-generated-menu-dialog',
  templateUrl: './generate-menu-dialog.component.html',
  styleUrls: ['./generate-menu-dialog.component.scss']
})
export class GenerateMenuDialogComponent implements OnInit {
  ingredients: IngredientSuggestionDto[] = [];
  currentMenu: number = 1; // the index of the ingredient that is currently selected, to show its cocktails
  menus: RecommendedMenues = null;
  @Output() result = new EventEmitter<boolean>(); // emits true if a ingredient was added, false if not (undefined if the dialog was closed)


  constructor(
    @Inject(MAT_DIALOG_DATA) public props: { groupId: number ,numberOfCocktails: number},
    private cocktailService: CocktailService,
    private errorFormatter: ErrorFormatterService,
    private notification: ToastrService,
  ) {
  }


  ngOnInit(): void {
    this.getGeneratedMenu(this.props.groupId, this.props.numberOfCocktails);

  }

  /**
   * Get the cocktails that can be made with the currently selected menu
   */
  get currentMenuCocktails() {
    return (this.menus.menuList.length > 0) ? this.menus.menuList[this.currentMenu] : null;
  }

  /**
   * Get suggestions for menues from the backend
   *
   * @param id The id of the group to get suggestions for
   */
  private getGeneratedMenu(id: number, numberOfCocktails: number) {
    this.cocktailService.generateCocktailMenu(id,numberOfCocktails).subscribe({
      next: (menu: RecommendedMenues) => {
        this.menus = menu;
      },
      error: error => {
        this.notification.error(error.details, error.error.detail, {
          enableHtml: true,
          timeOut: 10000,
        });
        this.result.emit(false);
      }
    });
  }

  /**
   * Sets the currently selected menu as the users menu and closes the dialog
   */
  addMenu() {
    let menu: MenuCocktailsDto = {
      groupId: this.menus.id,
      cocktailsList: this.menus.menuList[this.currentMenu].cocktailMenu,
    };
    this.cocktailService.saveCocktails(menu).subscribe({
      next: (menu: MenuCocktailsDto) => {
        this.notification.success("Successfully saved menu!");
        this.result.emit(true);
      },
      error: error => {
        console.error('Could not save cocktails due to:' + error.message);
        this.notification.error(this.errorFormatter.format(error), `Error setting menu.`, {
          enableHtml: true,
          timeOut: 10000,
        });
        this.result.emit(false);
      }
    });

  }
}
