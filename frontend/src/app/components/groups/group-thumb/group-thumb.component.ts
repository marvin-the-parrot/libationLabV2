import {Component, Input} from '@angular/core';
import {Group} from "../../../dtos/group";

@Component({
  selector: 'app-group-thumb',
  templateUrl: './group-thumb.component.html',
  styleUrls: ['./group-thumb.component.scss']
})
export class GroupThumbComponent {

  @Input() group: Group;

  createGroup() {
    // todo
  }
}
