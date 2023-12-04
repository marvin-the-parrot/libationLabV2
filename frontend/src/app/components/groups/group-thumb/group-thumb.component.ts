import {Component, Input} from '@angular/core';
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
            this.router.navigate(['/groups']);
          },
          error: error => {
            console.error(`Error leaving group.`, error);
            this.notification.error(`Error leaving group.`); // todo: show error message from backend
          }
        });
      } else {
        this.router.navigate(['/groups']);
      }
    });
  }
}
