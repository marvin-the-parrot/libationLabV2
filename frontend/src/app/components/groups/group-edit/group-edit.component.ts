import { Component } from '@angular/core';
import {NgForm, NgModel} from "@angular/forms";
import {GroupsService} from "../../../services/groups.service";
import {ToastrService} from "ngx-toastr";
import {DialogService} from "../../../services/dialog.service";
import {Router} from "@angular/router";
import {ErrorFormatterService} from "../../../services/error-formatter.service";
import {GroupOverview} from "../../../dtos/group-overview";
import {Observable, of} from "rxjs";
import {UserListDto, UserListGroupDto} from "../../../dtos/user";
import {UserService} from "../../../services/user.service";
import {ConfirmationDialogMode} from "../../../confirmation-dialog/confirmation-dialog.component";

@Component({
  selector: 'app-group-edit',
  templateUrl: './group-edit.component.html',
  styleUrls: ['./group-edit.component.scss']
})
export class GroupEditComponent {

  // todo: the following code is copied from group-create.component.ts and is just here so the html works

  group: GroupOverview = {
    id: 0,
    name: '',
    cocktails: [],
    members: [],
  }

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
    private errorFormatter: ErrorFormatterService,
  ) {
  }

  ngOnInit(): void {

    // add the user that creates the group first and make him the host
    var user = JSON.parse(localStorage.getItem('user'));
    if (user == null) {
      this.router.navigate(['/login']);
      return;
    }

    var host: UserListGroupDto = {
      id: user.id,
      name: user.name,
      isHost: true
    }

    this.group.members.push(host);
    this.group.host = host;

  }

  public onDelete(): void {
    this.dialogService.openConfirmationDialog(ConfirmationDialogMode.Delete).subscribe((result) => {
      if (result) {
        this.service.deleteGroup(this.group.id).subscribe({
          next: data => {
            this.notification.success(`Successfully deleted Group "${this.group.name}".`);
            this.router.navigate(['/groups']);
          },
          error: error => {
            console.error('Error deleting group', error);
            this.notification.error(`Error deleting group "${this.group.name}".`);
          }
        });
      }
    });
  }

  public onSubmit(form: NgForm) {
    console.log("is form valid?", form.valid, this.group);
    if (form.valid) {
      this.service.create(this.group).subscribe({
        next: data => {
          this.router.navigate(["/groups"]);
          this.notification.success(`Successfully created Group "${this.group.name}".`);
        },
        error: error => {
          console.error("Error creating/editing group", error);
          this.notification.error(this.errorFormatter.format(error), `Error creating group "${this.group.name}".`, {
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

  removeMember(index: number) {
    this.group.members.splice(index, 1);
  }

  // todo: end of the copied code

  // todo: leave group

}
