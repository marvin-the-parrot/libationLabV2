import {Component, EventEmitter, Input, Output} from '@angular/core';
import {GroupOverview} from "../../../dtos/group-overview";
import {UserListDto} from "../../../dtos/user";
import {DialogService} from "../../../services/dialog.service";
import {ConfirmationDialogMode} from "../../../confirmation-dialog/confirmation-dialog.component";
import {GroupsService} from "../../../services/groups.service";
import {Router} from "@angular/router";
import {ToastrService} from "ngx-toastr";
import {FeedbackService} from "../../../services/feedback.service";

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
    private notification: ToastrService,
    private feedbackService: FeedbackService
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
          next: () => {
            this.notification.success(`Successfully left Group '${this.group.name}'.`);
            this.groupLeft.emit();
            this.deleteFeedbackRelationsAtUserLeavingGroup(this.group.id, user.id);
          },
          error: error => {
            console.error(`Error leaving group.`, error);
            this.notification.error(error.error.detail, `Error leaving group.`, {
              enableHtml: true,
              timeOut: 10000,
            });
          }
        });
      }
    });
  }

  private deleteFeedbackRelationsAtUserLeavingGroup(groupId: number, memberId: number) {
    this.feedbackService.deleteFeedbackRelationsAtUserLeavingGroup(groupId, memberId).subscribe({
      next: () => {
        console.log(`Successfully removed unused feedbacks`);
      },
      error: error => {
        console.error(`Error removing unused feedback from user`, error);
      }
    })
  }
}
