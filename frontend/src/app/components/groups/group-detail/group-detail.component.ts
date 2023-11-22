import { Component } from '@angular/core';
import {Group} from "../../../dtos/group";
import {GroupsService} from "../../../services/groups.service";
import {ActivatedRoute} from "@angular/router";

@Component({
  selector: 'app-group-detail',
  templateUrl: './group-detail.component.html',
  styleUrls: ['./group-detail.component.scss']
})
export class GroupDetailComponent {

  group: Group = {
    id: 1,
    name: 'Cocktail Party',
    host: {name: 'Mr X', id: 1},
    cocktails: ['Mochito', 'Mai Tai', 'White Russian'],
    members: [{name: 'Sep', id: 4}, {name: 'Jan', id: 5}, {name: 'Peter', id: 6}, {name: 'Susanne', id: 7}],
  }

  constructor(
    private service: GroupsService,
    private route: ActivatedRoute,
  ) {
  }

  ngOnInit(): void {
    const groupId = this.route.snapshot.params['id'];
    this.service.getById(groupId).subscribe(
      (group: Group) => {
        this.group = group;
      },
      error => {
        console.error('Error fetching group details for editing', error);
        // Handle error appropriately (e.g., show a message to the user)
      }
    );
  }
}
