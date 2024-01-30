import {Component} from '@angular/core';
import {GroupOverview} from "../../../dtos/group-overview";
import {GroupsService} from "../../../services/groups.service";
import {ActivatedRoute} from "@angular/router";
import {Observable, of} from "rxjs";
import {UserListDto} from "../../../dtos/user";
import {UserService} from "../../../services/user.service";
import {MessageCreate} from "../../../dtos/message";
import {MessageService} from "../../../services/message.service";
import {ToastrService} from 'ngx-toastr';
import {DialogService} from 'src/app/services/dialog.service';
import {ConfirmationDialogMode} from "../../../confirmation-dialog/confirmation-dialog.component";
import {IngredientGroupDto} from "../../../dtos/ingredient";
import {IngredientService} from "../../../services/ingredient.service";
import {CocktailService} from 'src/app/services/cocktail.service';
import {MenuCocktailsDetailViewDto, MenuCocktailsDetailViewHostDto} from 'src/app/dtos/menu';
import {CocktailFeedbackDto, FeedbackState} from "../../../dtos/cocktail";
import {FeedbackService} from "../../../services/feedback.service";
import {Location} from "@angular/common";

@Component({
  selector: 'app-group-detail',
  templateUrl: './group-detail.component.html',
  styleUrls: ['./group-detail.component.scss']
})
export class GroupDetailComponent {

  group: GroupOverview = {
    id: null,
    name: null,
    cocktails: [],
    members: [],
  }

  menu: MenuCocktailsDetailViewDto = {
    groupId: null,
    cocktailsList: [],
  }

  menuHost: MenuCocktailsDetailViewHostDto = {
    groupId: null,
    cocktailsList: [],
  }

  username: string = JSON.parse(localStorage.getItem('user')).name;
  // for autocomplete
  user: UserListDto = {
    id: null,
    name: ''
  };

  ingredients: IngredientGroupDto[] = [];

  submitted = false;
  // Error flag
  error = false;

  constructor(
    private groupsService: GroupsService,
    private userService: UserService,
    private ingredientService: IngredientService,
    private dialogService: DialogService,
    private messageService: MessageService,
    private notification: ToastrService,
    private route: ActivatedRoute,
    private cocktailService: CocktailService,
    private feedbackService: FeedbackService,
    private location: Location
  ) {
  }

  ngOnInit(): void {
    const groupId = this.route.snapshot.params['id'];
    this.getGroup(groupId);
    this.getIngredients(groupId);
    console.log(this.group.cocktails);
  }

  memberSuggestions = (input: string): Observable<UserListDto[]> => (input === '')
    ? of([])
    : this.userService.searchUsersGroupExisting(input, this.route.snapshot.params['id']);

  public formatMember(member: UserListDto | null): string {
    return member?.name ?? '';
  }

  /**
   * Create Message
   */
  createMessage(userId: number) {
    if (userId == null) {
      return;
    }
    const groupId = this.route.snapshot.params['id'];
    this.submitted = true;
    const createMessage: MessageCreate = new MessageCreate(userId, groupId);
    this.create(createMessage);
  }

  getUsersOfIngredient(ingredient: IngredientGroupDto): string {
    let userIngredientString: string = ingredient.name + ' belongs to Users: ';
    ingredient.users.forEach(function (user) {
      userIngredientString += user.name + ', ';
    });
    return userIngredientString.slice(0, -2);
  }

  public openMemberOptions(member: UserListDto, event: MouseEvent): void {
    let position = {top: event.clientY + 'px', left: event.clientX + 'px'}; // open dialog at mouse position
    this.dialogService.openOptionDialog(position).subscribe((deleteOption) => {
      // if true -> remove member, if false -> make member host, if undefined -> do nothing (dialog was closed)
      if (deleteOption) {
        this.removeMemberFromGroup(member);
      } else if (deleteOption === false) {
        this.makeMemberHost(member);
      }
    });
  }

  private removeMemberFromGroup(member: UserListDto) {
    this.dialogService.openConfirmationDialog(ConfirmationDialogMode.RemoveUser).subscribe((result) => {
      if (result) {
        this.groupsService.removeMemberFromGroup(this.group.id, member.id).subscribe({
          next: () => {
            this.notification.success(`Successfully removed '${member.name}' from Group '${this.group.name}'.`);
            this.getGroup(this.group.id); // refresh group
            this.deleteFeedbackRelationsAtUserLeavingGroup(this.group.id, member.id);
          },
          error: error => {
            console.error(`Error removing member '${member.name}' from group.`, error);
            this.notification.error(error.error.detail, `Error removing member '${member.name}' from group.`, {
              enableHtml: true,
              timeOut: 10000,
            });
          }
        });
      }
    });
  }

  private deleteFeedbackRelationsAtUserLeavingGroup(groupId: number, memberId: number) {
    this.feedbackService.deleteFeedbackRelationsAtUserLeavingGroup(groupId, memberId).subscribe({
      next: () => {
        console.log(`Successfully removed feedback from user`);
      },
      error: error => {
        console.error(`Error removing unused feedback from user`, error);
      }
    })
  }

  private makeMemberHost(member: UserListDto) {
    this.dialogService.openConfirmationDialog(ConfirmationDialogMode.MakeHost).subscribe((result) => {
      if (result) {

        this.groupsService.makeMemberHost(this.group.id, member.id).subscribe({
          next: () => {
            this.notification.success(`Successfully made '${member.name}' host of Group '${this.group.name}'.`);
            this.getGroup(this.group.id); // refresh group
          },
          error: error => {
            console.error(`Error making member '${member.name}' host of group.`, error);
            this.notification.error(error.error.detail, `Error making member '${member.name}' host of group.`, {
              enableHtml: true,
              timeOut: 10000,
            });
          }
        });
      }
    });
  }

  /**
   * Send Creat data to the backend if it was successfully, the user will be forwarded
   *
   * @param messageCreate create account data from the form
   */
  create(messageCreate: MessageCreate) {
    this.messageService.createMessage(messageCreate).subscribe({
      next: () => {
        this.notification.success('Successfully invited ' + this.user.name + ' to ' + this.group.name + '!');
      },
      error: error => {
        console.log('Could not create message due to:');
        this.defaultServiceErrorHandling(error);
      }
    });
  }

  /**
   * Get group data by id. Used to initially get the group and refresh it after a change.
   *
   * @param id the id of the group
   */
  private getGroup(id: number) {
    this.groupsService.getById(id).subscribe({
      next: (group: GroupOverview) => {
        this.group = group;
        console.log(this.group)

        const groupId = this.route.snapshot.params['id'];
        if (this.username == this.group.host.name) {
          this.getCocktailsMenuHost(groupId);
          //this.getCocktailsMenu(groupId);
        } else {
          this.getCocktailsMenu(groupId);
        }

      },
      error: error => {
        console.error('Could not fetch group due to:', error);
        this.error = true;
        const displayError = error.error.errors != null ? error.error.errors : error.error;
        this.notification.error(displayError, "Error fetching group", {
          enableHtml: true,
          timeOut: 10000,
        });
        this.location.back();
      }
    });
  }

  private getIngredients(groupId: number): void {
    this.ingredientService.getAllGroupIngredients(groupId).subscribe({
      next: (ingredients: IngredientGroupDto[]) => {
        this.ingredients = ingredients;
      },
      error: error => {
        console.error('Could not fetch ingredients due to:', error);
      }
    });
  }

  private getCocktailsMenu(groupId: number): void {
    this.cocktailService.getCocktailMenuDetailView(groupId).subscribe({
      next: (menu: MenuCocktailsDetailViewDto) => {
        this.menu = menu;
      },
      error: () => {
        console.error('Could not fetch cocktails menu');
      }
    });
  }

  private getCocktailsMenuHost(groupId: number): void {
    this.cocktailService.getCocktailMenuDetailViewHost(groupId).subscribe({
      next: (menu: MenuCocktailsDetailViewHostDto) => {
        this.menuHost = menu;
      },
      error: () => {
        console.error('Could not fetch cocktails menu');
      }
    });
  }

  private defaultServiceErrorHandling(error: any) {
    console.log(error);
    this.error = true;
    this.notification.error(error.error.detail);
  }

  /**
   * Opens the cocktails details in a modal.
   * @param id The id of the cocktail to open
   */
  openCocktailDetails(id: number) {
    this.dialogService.openCocktailDetailDialog(id).subscribe({
      next: () => {
        console.log("Successfully opened cocktail details");
      },
      error: error => {
        console.error('Could not open cocktail details due to:');
        this.notification.error(error.error.detail);
      }
    });
  }

  likeCocktail(cocktailId: number) {

    const cocktailFeedback: CocktailFeedbackDto = {
      cocktailId: cocktailId,
      groupId: this.group.id,
      rating: FeedbackState.Like
    }

    for (let cocktail of this.menu.cocktailsList) {
      if (cocktail.id == cocktailId) {
        cocktail.rating = FeedbackState.Like;
      }
    }

    this.feedbackService.updateCocktailFeedback(cocktailFeedback).subscribe({
      next: () => {
        this.notification.success("Successfully liked cocktail");
      },
      error: () => {
        console.error('Like did not work, something went wrong');
        this.notification.error('Like did not work, something went wrong');
      }
    });

  }

  dislikeCocktail(cocktailId: number) {
    const cocktailFeedback: CocktailFeedbackDto = {
      cocktailId: cocktailId,
      groupId: this.group.id,
      rating: FeedbackState.Dislike
    }

    for (let cocktail of this.menu.cocktailsList) {
      if (cocktail.id == cocktailId) {
        cocktail.rating = FeedbackState.Dislike;
      }
    }

    this.feedbackService.updateCocktailFeedback(cocktailFeedback).subscribe({
      next: () => {
        this.notification.success("Successfully disliked cocktail");
      },
      error: ()=> {
        console.error('Dislike did not work, something went wrong');
        this.notification.error('Like did not work, something went wrong');
      }
    });
  }

  calculatePositiveBar (positiveRating: number, negativeRating: number): string {
    const total = positiveRating + negativeRating;
    if (total == 0) {
      return '0%';
    }
    const positive = (positiveRating / total) * 100;
    return positive + '%';
  }

  calculateNegativeBar (positiveRating: number, negativeRating: number): string {
    const total = positiveRating + negativeRating;
    if (total == 0) {
      return '0%';
    }
    const negative = (negativeRating / total) * 100;
    return negative + '%';
  }

  getTooltipText(positiveRating: number, negativeRating: number): string {
    return `Positive: ${positiveRating}, Negative: ${negativeRating}`;
  }

}




