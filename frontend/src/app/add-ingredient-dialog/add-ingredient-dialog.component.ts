import {Component, Inject, OnInit} from '@angular/core';
import {GroupsService} from "../services/groups.service";
import {MAT_DIALOG_DATA} from "@angular/material/dialog";
import {IngredientService} from "../services/ingredient.service";
import {IngredientSuggestionDto} from "../dtos/ingredient";

@Component({
  selector: 'app-add-ingredient-dialog',
  templateUrl: './add-ingredient-dialog.component.html',
  styleUrls: ['./add-ingredient-dialog.component.scss']
})
export class AddIngredientDialogComponent implements OnInit {
  backgroundColor = '#bb90f2';
  ingredients: IngredientSuggestionDto[] = [];
  currentIngredient: number = 0; // the index of the ingredient that is currently selected, to show its cocktails


  constructor(
    @Inject(MAT_DIALOG_DATA) public groupId: { groupId: number },
    private groupsService: GroupsService,
    private ingredientService: IngredientService,
  ) {
  }


  ngOnInit(): void {
    console.log(`Getting ingredient suggestions for ${this.groupId.groupId}`);

    this.getIngredientSuggestions(this.groupId.groupId);

  }

  /**
   * Get the cocktails that can be made with the currently selected ingredient
   */
  get currentIngredientCocktails() {
    return (this.ingredients.length > 0) ? this.ingredients[this.currentIngredient].possibleCocktails : null;
  }


  /**
   * Get suggestions for ingredients from the backend
   *
   * @param id The id of the group to get suggestions for
   */
  private getIngredientSuggestions(id: number) {
    this.ingredientService.getIngredientSuggestions(id).subscribe({
      next: (ingredients: IngredientSuggestionDto[]) => {
        this.ingredients = ingredients;
        console.log("Successfully fetched ingredients: ");
        console.log(ingredients);
      },
      error: error => {
        console.error('Could not fetch ingredients due to:' + error.message);
      }
    });
  }
}
