import {Component, EventEmitter, Inject, OnInit, Output} from '@angular/core';
import {MAT_DIALOG_DATA} from "@angular/material/dialog";
import {IngredientService} from "../services/ingredient.service";
import {IngredientListDto, IngredientSuggestionDto} from "../dtos/ingredient";
import {ToastrService} from "ngx-toastr";
import {ErrorFormatterService} from "../services/error-formatter.service";

@Component({
    selector: 'app-add-ingredient-dialog',
    templateUrl: './add-ingredient-dialog.component.html',
    styleUrls: ['./add-ingredient-dialog.component.scss']
})
export class AddIngredientDialogComponent implements OnInit {
    ingredients: IngredientSuggestionDto[] = [];
    currentIngredient: number = 0; // the index of the ingredient that is currently selected, to show its cocktails

    @Output() result = new EventEmitter<boolean>(); // emits true if an ingredient was added, false if not (undefined if the dialog was closed)


    constructor(
        @Inject(MAT_DIALOG_DATA) public groupId: { groupId: number },
        private ingredientService: IngredientService,
        private errorFormatter: ErrorFormatterService,
        private notification: ToastrService,
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
     * Adds the currently selected ingredient to the user's ingredients and closes the dialog
     */
    addIngredient() {
        let ingredient: IngredientListDto = {
            id: this.ingredients[this.currentIngredient].id,
            name: this.ingredients[this.currentIngredient].name,
        };
        this.ingredientService.addIngredientSuggestion(ingredient).subscribe({
            next: (ingredients: IngredientListDto[]) => {
                console.log("Successfully fetched ingredients: ");
                console.log(ingredients);
                this.notification.success("Successfully added ingredient " + ingredient.name);
                this.result.emit(true);
            },
            error: error => {
                console.error('Could not fetch ingredients due to:' + error.message);
                this.notification.error(this.errorFormatter.format(error), `Error adding ingredient.`, {
                    enableHtml: true,
                    timeOut: 10000,
                });
                this.result.emit(false);
            }
        });

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
                this.notification.error(this.errorFormatter.format(error), `Error fetching ingredients.`, {
                    enableHtml: true,
                    timeOut: 10000,
                });
                this.result.emit(false);
            }
        });
    }
}
