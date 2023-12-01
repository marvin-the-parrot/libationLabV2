import {Component, OnInit} from '@angular/core';
import {AuthService} from "../../services/auth.service";
import {GroupOverview} from "../../dtos/group-overview";
import {GroupsService} from "../../services/groups.service";
import {MessageDetailDto} from "../../dtos/message";

@Component({
  selector: 'app-groups',
  templateUrl: './groups.component.html',
  styleUrls: ['./groups.component.scss']
})
export class GroupsComponent implements OnInit {

  // todo: replace with real data
  groups: GroupOverview[] = null;


  constructor(
    public authService: AuthService,
    public groupService: GroupsService,
  ) {
  }

  ngOnInit() {
    this.fetchGroups();
  }

  private fetchGroups() {
    this.groupService.getAllByUser().subscribe({
      next: (groups: GroupOverview[]) => {
        for (let group of groups) {
          if (group.cocktails==null){
            group.cocktails=["Mojito", "Manhatten", "Old Fashioned", "B52"];
          }
        }
        this.groups = groups;
      },
      error: error => {
        console.error('Error fetching groups', error);
        // Handle error appropriately (e.g., show a message to the user)
      }
  });
  }

}
