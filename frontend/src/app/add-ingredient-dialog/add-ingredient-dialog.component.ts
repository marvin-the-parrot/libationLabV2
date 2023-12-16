import {Component, Inject} from '@angular/core';
import {CocktailOverviewDto} from "../dtos/cocktail";
import {GroupsService} from "../services/groups.service";
import {MAT_DIALOG_DATA} from "@angular/material/dialog";

@Component({
  selector: 'app-add-ingredient-dialog',
  templateUrl: './add-ingredient-dialog.component.html',
  styleUrls: ['./add-ingredient-dialog.component.scss']
})
export class AddIngredientDialogComponent {
  backgroundColor = '#bb90f2';
  ingredients = [];
  cocktails: CocktailOverviewDto[] = []


  constructor(
    @Inject(MAT_DIALOG_DATA) public groupId: { groupId: number },
    private groupsService: GroupsService,
  ) {
  }


  ngOnInit(): void {
    console.log(this.groupId.groupId)
    this.getGroup(this.groupId.groupId);
  }

  /**
   * Get group data by id. Used to initially get the group and refresh it after a change.
   *
   * @param id the id of the group
   */
  private getGroup(id: number) {
    this.groupsService.getMixables(id).subscribe({
      next: (cocktails: CocktailOverviewDto[]) => {
        this.cocktails = cocktails;
        console.log(this.cocktails)
      },
      error: error => {
        console.error('Could not fetch cocktails due to:');
        // todo: Handle error appropriately (e.g., show a message to the user)
      }
    });
  }
}
