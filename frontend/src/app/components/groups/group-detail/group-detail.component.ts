import { Component } from '@angular/core';
import {Group} from "../../../dtos/group";
import {GroupService} from "../../../services/group.service";
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
    host: 'Mr X',
    cocktails: ['Mochito', 'Mai Tai', 'White Russian'],
    numMembers: 15
  }

  constructor(
    private service: GroupService,
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
