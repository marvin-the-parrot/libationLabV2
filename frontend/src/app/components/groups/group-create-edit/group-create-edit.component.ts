import {Component, OnInit} from '@angular/core';
import {GroupOverview} from "../../../dtos/group-overview";
import {GroupsService} from "../../../services/groups.service";
import {Router} from "@angular/router";
import {NgForm, NgModel} from "@angular/forms";
import {Observable, of} from "rxjs";
import {UserListDto, UserListGroupDto} from "../../../dtos/user";
import {UserService} from "../../../services/user.service";

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

  user: UserListDto = {
    id: null,
    name: ''
  };

  dummyMemberSelectionModel: unknown; // Just needed for the autocomplete

  constructor(
    private service: GroupsService,
    private userService: UserService,
    private router: Router
  ) {
  }

  ngOnInit(): void {
  }

  public get heading(): string {
    switch (this.mode) {
      case GroupCreateEditMode.create:
        return 'Create GroupOverview';
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
    : this.userService.search(input);

  public formatMember(member: UserListDto | null): string {
    return member?.name ?? '';
  }

  public dynamicCssClassesForInput(input: NgModel): any {
    return {
      'is-invalid': !input.valid && !input.pristine,
    }
  }

  public addMember(user: UserListGroupDto | null) {
    if (user == null)
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
    })
  }

  removeMember(index: number) {
    this.group.members.splice(index, 1);
  }
}
