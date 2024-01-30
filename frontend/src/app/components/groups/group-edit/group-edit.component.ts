import { Component } from '@angular/core';
import {NgForm, NgModel} from "@angular/forms";
import {GroupsService} from "../../../services/groups.service";
import {ToastrService} from "ngx-toastr";
import {DialogService} from "../../../services/dialog.service";
import {ActivatedRoute, Router} from "@angular/router";
import {ErrorFormatterService} from "../../../services/error-formatter.service";
import {GroupOverview} from "../../../dtos/group-overview";
import {Observable, of} from "rxjs";
import {UserListDto, UserListGroupDto} from "../../../dtos/user";
import {UserService} from "../../../services/user.service";
import {ConfirmationDialogMode} from "../../../confirmation-dialog/confirmation-dialog.component";
import {Location} from "@angular/common";

@Component({
  selector: 'app-group-edit',
  templateUrl: './group-edit.component.html',
  styleUrls: ['./group-edit.component.scss']
})
export class GroupEditComponent {

  group: GroupOverview = {
    id: 0,
    name: 'Loading...',
    cocktails: [],
    members: [],
  }

  membersSelectedForDeletion: boolean[] = []; // saves which members are selected to be removed from the group

  user: UserListGroupDto = {
    id: null,
    name: '',
    isHost: false
  };

  dummyMemberSelectionModel: unknown; // Just needed for the autocomplete

  constructor(
    private service: GroupsService,
    private userService: UserService,
    private dialogService: DialogService,
    private notification: ToastrService,
    private router: Router,
    private route: ActivatedRoute,
    private errorFormatter: ErrorFormatterService,
    private location: Location,
  ) {
  }

  ngOnInit(): void {
    const groupId = this.route.snapshot.params['id'];
    this.getGroup(groupId);
  }

  /**
   * Delete the group.
   */
  public onDelete(): void {
    this.dialogService.openConfirmationDialog(ConfirmationDialogMode.DeleteGroup).subscribe((result) => {
      if (result) {
        this.service.deleteGroup(this.group.id).subscribe({
          next: () => {
            this.notification.success(`Successfully deleted Group "${this.group.name}".`);
            this.router.navigate(['/groups']);
          },
          error: error => {
            console.error('Error deleting group', error);
            this.notification.error(error.error.detail, `Error deleting group "${this.group.name}".`, {
              enableHtml: true,
              timeOut: 10000,
            });
          }
        });
      }
    });
  }

  /**
   * Save edited group.
   *
   * @param form
   */
  public onSubmit(form: NgForm) {
    console.log("is form valid?", form.valid, this.group);
    if (form.valid) {
      this.service.update(this.group).subscribe({
        next: data => {
          this.notification.success(`Successfully edited Group "${this.group.name}".`);
          this.router.navigate([data.id, 'detail'], { relativeTo: this.route.parent });
        },
        error: error => {
          console.error("Error editing group", error);
          this.notification.error(this.errorFormatter.format(error), `Error editing group "${this.group.name}".`, {
            enableHtml: true,
            timeOut: 10000,
          });
        }
      });

    }
  }

  memberSuggestions = (input: string): Observable<UserListDto[]> => (input === '')
    ? of([])
    : this.userService.searchUsersGroupCreating(input, this.group.members);

  public formatMember(member: UserListDto | null): string {
    return member?.name ?? '';
  }

  public dynamicCssClassesForInput(input: NgModel): any {
    return {
      'is-invalid': !input.valid && !input.pristine,
    }
  }

  /**
   * Add a member to the group.
   *
   * @param user the user that should be added to the group
   */
  public addMember(user: UserListGroupDto | null) {

    if (user == null || user.id == null)
      return;

    setTimeout(() => {
      for (let i = 0; i < this.group.members.length; i++) {
        if (this.group.members[i]?.id === user.id) {
          // show error message: duplicate member
          this.notification.error(`User "${user.name}" is already a member of this group.`);
          this.dummyMemberSelectionModel = null;
          return;
        }
      }
      this.group.members.push(user);
      this.user = null;
    })
  }

  /**
   * Get group data by id. Used to initially get the group and refresh it after a change.
   *
   * @param id the id of the group
   */
  private getGroup(id: number) {
    this.service.getById(id).subscribe({
      next: (group: GroupOverview) => {
        this.group = group;
        this.membersSelectedForDeletion = new Array(this.group.members.length).fill(false);
      },
      error: error => {
        console.error('Error fetching group', error);
        const displayError = error.error.errors != null ? error.error.errors : error.error;
        this.notification.error(displayError, `Error fetching group.`, {
          enableHtml: true,
          timeOut: 10000,
        });
        this.location.back(); // Go back to the previous page
      }
    });
  }

  /**
   * Returns true if at least one member is selected for deletion.
   */
  isAnyMemberSelected(): boolean {
    return this.membersSelectedForDeletion.some((value) => value);
  }

  /**
   * Removes all members that are selected for deletion. (This does not save the changes to the backend; the user has to click "Save" for that.)
   */
  removeSelectedMembers() {
    this.dialogService.openConfirmationDialog(ConfirmationDialogMode.RemoveUsers).subscribe((result) => {
      if (result) {
        for (let i = 0; i < this.membersSelectedForDeletion.length; i++) {
          if (this.membersSelectedForDeletion[i]) {
            this.group.members.splice(i, 1);
            this.membersSelectedForDeletion.splice(i, 1);
            i--; // because we just removed an element
          }
        }
      }
    });
  }
}
