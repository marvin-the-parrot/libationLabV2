import {Component, Input} from '@angular/core';
import {GroupOverview} from "../../../dtos/group-overview";

@Component({
  selector: 'app-group-thumb',
  templateUrl: './group-thumb.component.html',
  styleUrls: ['./group-thumb.component.scss']
})
export class GroupThumbComponent {

  // get the group to display from the parent component
  @Input() group: GroupOverview;
}
