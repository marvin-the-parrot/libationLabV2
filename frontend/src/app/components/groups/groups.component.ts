import {Component, OnInit} from '@angular/core';
import {AuthService} from "../../services/auth.service";
import {Group} from "../../dtos/group";

@Component({
  selector: 'app-groups',
  templateUrl: './groups.component.html',
  styleUrls: ['./groups.component.scss']
})
export class GroupsComponent implements OnInit {

  // todo: replace with real data
  groups: Group[] = [new Group(1, "Cocktail Party", "Mr X", ["Mochito", "Mai Tai", "White Russian"], 15),
    new Group(2, "Party People", "Mr Y", ["Mai Tai", "Mai Tai", "White Russian"], 7),
    new Group(3, "Friends", "Mrs Z", ["Mochito", "Mai Tai", "White Russian"], 3)]


  constructor(public authService: AuthService) {
  }

  ngOnInit() {
  }

}
