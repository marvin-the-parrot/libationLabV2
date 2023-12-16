import { Component } from '@angular/core';
import {IngredientGroupDto} from "../../../dtos/ingredient";
import {GroupsService} from "../../../services/groups.service";
import {UserService} from "../../../services/user.service";
import {IngredientService} from "../../../services/ingredient.service";
import {DialogService} from "../../../services/dialog.service";
import {MessageService} from "../../../services/message.service";
import {ToastrService} from "ngx-toastr";
import {ActivatedRoute, Router} from "@angular/router";
import {CocktailOverviewDto} from "../../../dtos/cocktail";

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

  constructor(
    private groupsService: GroupsService,
    private userService: UserService,
    private ingredientService: IngredientService,
    private dialogService: DialogService,
    private messageService: MessageService,
    private notification: ToastrService,
    private route: ActivatedRoute,
    private router: Router,
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

  openAddIngredientModal() {
    this.dialogService.openAddIngredientDialog(this.groupId).subscribe((result) => {
      // todo: Handle result
    });
  }
}


