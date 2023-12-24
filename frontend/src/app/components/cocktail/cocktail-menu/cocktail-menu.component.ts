import {Component} from '@angular/core';
import {IngredientGroupDto} from "../../../dtos/ingredient";
import {GroupsService} from "../../../services/groups.service";
import {UserService} from "../../../services/user.service";
import {IngredientService} from "../../../services/ingredient.service";
import {DialogService} from "../../../services/dialog.service";
import {CocktailService} from 'src/app/services/cocktail.service';
import {MessageService} from "../../../services/message.service";
import {ToastrService} from "ngx-toastr";
import {ActivatedRoute, Router} from "@angular/router";
import {CocktailListDto, CocktailOverviewDto} from "../../../dtos/cocktail";
import {ErrorFormatterService} from "../../../services/error-formatter.service";

@Component({
    selector: 'app-cocktail-card',
    templateUrl: './cocktail-menu.component.html',
    styleUrls: ['./cocktail-menu.component.scss']
})
export class CocktailMenuComponent {
    cocktails: CocktailOverviewDto[] = []
    ingredients: IngredientGroupDto[] = [];
    groupId: number;
    dummyMemberSelectionModel: unknown; // Just needed for the autocomplete
    submitted = false;
    // Error flag
    error = false;
    errorMessage = '';
    cocktails_list: CocktailListDto[] = [];
    nameOfCocktail: string;
    nameOfIngredient: string;
    nameOfPreference: string;
    bannerError: string | null = null;
    selectedCocktails: string[] = [];

    constructor(
        private groupsService: GroupsService,
        private userService: UserService,
        private ingredientService: IngredientService,
        private dialogService: DialogService,
        private messageService: MessageService,
        private notification: ToastrService,
        private route: ActivatedRoute,
        private router: Router,
        private errorFormatter: ErrorFormatterService,
        private cocktailService: CocktailService,
    ) {
    }

    ngOnInit(): void {
        this.groupId = this.route.snapshot.params['id'];
        this.getGroup();
    }

    /**
     * Get group data by the id from the route. Used to initially get the group and refresh it after a change.
     */
    private getGroup() {
        this.groupsService.getMixables(this.groupId).subscribe({
            next: (cocktails: CocktailOverviewDto[]) => {
                this.cocktails = cocktails;
                console.log(this.cocktails)
            },
            error: error => {
                console.error('Could not fetch cocktails due to:');
                this.defaultServiceErrorHandling(error);
                // todo: Handle error appropriately (e.g., show a message to the user)
            }
        });
    }

    private defaultServiceErrorHandling(error: any) {
        console.log(error);
        this.error = true;
        this.notification.error(error.error.detail);
    }

    /**
     * Opens the ingredient suggestion dialog to let the user add a new ingredient
     */
    openAddIngredientModal() {
        this.dialogService.openAddIngredientDialog(this.groupId).subscribe((result: boolean) => {
            console.log("Added new ingredient: " + result); // true if added, false if error while adding, undefined if the user just closed the modal
            if (result === true) {
                this.getGroup();
            }
        });
    }

    
  searchChanged() {

    if((this.nameOfCocktail && this.nameOfCocktail.length != 0) || (this.nameOfIngredient && this.nameOfIngredient.length != 0) || (this.nameOfPreference && this.nameOfPreference.length != 0)){
      this.cocktailService.searchCocktails(this.nameOfCocktail, this.nameOfIngredient, this.nameOfPreference)
      .subscribe({
        next: data => {
          this.cocktails_list = data;
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
      this.cocktails_list = [];
    }

  }

  clickOnCocktailImage(name: string): void {
    const index = this.selectedCocktails.indexOf(name);
  
    if (index !== -1) {
      // The cocktail is already selected, remove it from the list
      this.selectedCocktails.splice(index, 1);
    } else {
      // The cocktail was not selected, add it to the list
      this.selectedCocktails.push(name);
    }
  }

  isSelected(name: string): boolean {
    return this.selectedCocktails.includes(name);
  }

  removeCocktail(index: number) {
    this.selectedCocktails.splice(index, 1);
  }

  saveCocktails() {
    this.cocktailService.saveCocktails(this.cocktails_list).subscribe({
      next: () => {
        this.notification.success('Cocktails saved successfully.');
      },
      error: error => {
        this.notification.error('Could not save cocktails.');
        console.log('Could not save cocktails due to:');
        console.log(error);
      }
    });
  }
  
}


