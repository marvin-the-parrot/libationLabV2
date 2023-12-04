import {Component, OnInit} from '@angular/core';
import {GroupOverview} from "../../../dtos/group-overview";
import {GroupsService} from "../../../services/groups.service";
import {Router} from "@angular/router";
import {NgForm, NgModel} from "@angular/forms";
import {ToastrService} from 'ngx-toastr';
import {Observable, of} from "rxjs";
import {UserListDto, UserListGroupDto} from "../../../dtos/user";
import {UserService} from "../../../services/user.service";
import { DialogService } from 'src/app/services/dialog.service';

export enum GroupCreateEditMode {
  create,
  edit,
}

@Component({
  selector: 'app-group-create-edit',
  templateUrl: './group-create-edit.component.html',
  styleUrls: ['./group-create-edit.component.scss']
})
export class GroupCreateEditComponent implements OnInit {

  mode: GroupCreateEditMode = GroupCreateEditMode.create;

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
    private router: Router
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

  public get heading(): string {
    switch (this.mode) {
      case GroupCreateEditMode.create:
        return 'Create Group';
      case GroupCreateEditMode.edit:
        return `Edit: ${this.group.name}`;
      default:
        return "?";
    }
  }

  public get submitButtonText(): string {
    switch (this.mode) {
      case GroupCreateEditMode.create:
        return 'Create';
      case GroupCreateEditMode.edit:
        return 'Save';
      default:
        return "?";
    }
  }

  get modeIsCreate(): boolean {
    return this.mode === GroupCreateEditMode.create;
  }

  public onDelete(): void {
    this.dialogService.openDeleteConfirmation().subscribe((result) => {
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
      let observable: Observable<GroupOverview>;

      switch (this.mode) {
        case GroupCreateEditMode.create:
          observable = this.service.create(this.group);
          break;
        case GroupCreateEditMode.edit:
          observable = this.service.update(this.group);
          break;
        default:
          console.log("unknown mode", this.mode);
          return;
      }

      observable.subscribe({
          next: data => {
            // todo: show success message
            this.router.navigate(["/groups"]);
          },
        error: error => {
            console.error("Error creating/editing group", error);
            // todo: show error message
        }
        });

    }
  }

  memberSuggestions = (input: string): Observable<UserListDto[]> => (input === '')
    ? of([])
    : this.userService.search(input, this.group.id);

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
          // todo: show error message: duplicate member
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
}
