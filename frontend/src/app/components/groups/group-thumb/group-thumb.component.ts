import {Component, EventEmitter, Input, Output} from '@angular/core';
import {GroupOverview} from "../../../dtos/group-overview";
import {UserListDto} from "../../../dtos/user";
import {DialogService} from "../../../services/dialog.service";
import {ConfirmationDialogMode} from "../../../confirmation-dialog/confirmation-dialog.component";
import {GroupsService} from "../../../services/groups.service";
import {Router} from "@angular/router";
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-group-thumb',
  templateUrl: './group-thumb.component.html',
  styleUrls: ['./group-thumb.component.scss']
})
export class GroupThumbComponent {

  // get the group to display from the parent component
  @Input() group: GroupOverview;
  @Input() username: string;
  @Output() groupLeft = new EventEmitter<void>(); // to send signal to parent component to reload groups, when user leaves a group

  constructor(
    private dialogService: DialogService,
    private service: GroupsService,
    private router: Router,
    private notification: ToastrService
  ) {
  }


  leaveGroup() {
    const user: UserListDto = JSON.parse(localStorage.getItem('user'));
    if (user == null) {
      this.router.navigate(['/login']);
      return;
    }
    this.dialogService.openConfirmationDialog(ConfirmationDialogMode.Leave).subscribe((result) => {
      if (result) {
        this.service.removeMemberFromGroup(this.group.id, user.id).subscribe({
          next: data => {
            this.notification.success(`Successfully left Group '${this.group.name}'.`);
            this.groupLeft.emit();
          },
          error: error => {
            console.error(`Error leaving group.`, error);
            this.notification.error(`Error leaving group.`); // todo: show error message from backend
          }
        });
      }
    });
  }
}
