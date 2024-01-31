import {Component, OnInit} from '@angular/core';
import {GroupOverview} from "../../../dtos/group-overview";
import {GroupsService} from "../../../services/groups.service";
import {Router} from "@angular/router";
import {NgForm, NgModel} from "@angular/forms";
import {ToastrService} from 'ngx-toastr';
import {Observable, of} from "rxjs";
import {UserListDto, UserListGroupDto} from "../../../dtos/user";
import {UserService} from "../../../services/user.service";
import {ErrorFormatterService} from "../../../services/error-formatter.service";

@Component({
  selector: 'app-group-create-edit',
  templateUrl: './group-create.component.html',
  styleUrls: ['./group-create.component.scss']
})
export class GroupCreateComponent implements OnInit {

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
    private notification: ToastrService,
    private router: Router,
    private errorFormatter: ErrorFormatterService,
  ) {
  }

  ngOnInit(): void {

    // add the user that creates the group first and make him the host
    const user = JSON.parse(localStorage.getItem('user'));
    if (user == null) {
      this.router.navigate(['/login']);
      return;
    }

    const host: UserListGroupDto = {
      id: user.id,
      name: user.name,
      isHost: true
    };

    this.group.members.push(host);
    this.group.host = host;

  }


  public onSubmit(form: NgForm) {
    console.log("is form valid?", form.valid, this.group);
    if (form.valid) {
      this.service.create(this.group).subscribe({
        next: () => {
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

  //TODO pass the existing members to the autocomplete
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
}
