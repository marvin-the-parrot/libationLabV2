import {Component, OnInit} from '@angular/core';
import {AuthService} from "../../services/auth.service";
import {Group} from "../../dtos/group";
import {GroupsService} from "../../services/groups.service";

@Component({
  selector: 'app-groups',
  templateUrl: './groups.component.html',
  styleUrls: ['./groups.component.scss']
})
export class GroupsComponent implements OnInit {

  // todo: replace with real data
  groups: Group[] = [new Group(1, "Cocktail Party", {
    name: "Mr X",
    id: 1
  }, ["Mochito", "Mai Tai", "White Russian"], [{name: "Sep", id: 4}, {name: "Jan", id: 5}, {
    name: "Peter",
    id: 6
  }, {name: "Susanne", id: 7}]),
    new Group(2, "Party People", {name: "Mr Y", id: 2}, ["Mai Tai", "Mai Tai", "White Russian"], [{
      name: "Jürgen",
      id: 9
    }, {name: "Hanz", id: 10}, {name: "Sibille", id: 11}, {name: "Rafael", id: 12}]),
    new Group(3, "Friends", {name: "Mrs Z", id: 3}, ["Mochito", "Mai Tai", "White Russian"], [{
      name: "Jürgen",
      id: 9
    }, {name: "Petra", id: 14}, {name: "Mark", id: 13}, {name: "Rafael", id: 12}])];


  constructor(
    public authService: AuthService,
    public service: GroupsService,
  ) {
  }

  ngOnInit() {
  }

}
