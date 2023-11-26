import { Component } from '@angular/core';
import {GroupOverview} from "../../../dtos/group-overview";
import {GroupsService} from "../../../services/groups.service";
import {ActivatedRoute} from "@angular/router";
import {Observable} from "rxjs";
import {UserListDto} from "../../../dtos/user";
import {NgModel} from "@angular/forms";
import {UserService} from "../../../services/user.service";

@Component({
  selector: 'app-group-detail',
  templateUrl: './group-detail.component.html',
  styleUrls: ['./group-detail.component.scss']
})
export class GroupDetailComponent {

  group: GroupOverview = {
    id: 1,
    name: 'Cocktail Party',
    host: {name: 'Mr X', id: 1},
    cocktails: ['Mochito', 'Mai Tai', 'White Russian'],
    members: [{name: 'Sep', id: 4}, {name: 'Jan', id: 5}, {name: 'Peter', id: 6}, {name: 'Susanne', id: 7}],
  }

  user: UserListDto;

  dummyMemberSelectionModel: unknown; // Just needed for the autocomplete

  constructor(
    private service: GroupsService,
    private userService: UserService,
    private route: ActivatedRoute
  ) {
  }

  ngOnInit(): void {
    const groupId = this.route.snapshot.params['id'];
    this.service.getById(groupId).subscribe(
      (group: GroupOverview) => {
        this.group = group;
      },
      error => {
        console.error('Error fetching group details for editing', error);
        // Handle error appropriately (e.g., show a message to the user)
      }
    );
  }

  memberSuggestions = (input: string) : Observable<UserListDto[]> =>
    this.userService.search({name: input, limit: 5});

  public formatMember(member: UserListDto | null): string {
    this.user = member;
    return !member
      ? ""
      : `${member.name}`
  }

  public dynamicCssClassesForInput(input: NgModel): any {
    return {
      'is-invalid': !input.valid && !input.pristine,
    }
  }

  public addMember(user: UserListDto | null) {
    if (user == null)
      return;

    this.service
  }

  /*public addMember(user: UserListDto | null) {
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
  }*/

  removeMember(index: number) {
    this.group.members.splice(index, 1);
  }
}
