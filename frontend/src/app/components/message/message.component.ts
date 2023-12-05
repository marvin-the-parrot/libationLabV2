import {Component, OnInit} from '@angular/core';
import {MessageService} from '../../services/message.service';
import {MessageDetailDto} from "../../dtos/message";
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-message',
  templateUrl: './message.component.html',
  styleUrls: ['./message.component.scss']
})
export class MessageComponent implements OnInit {

  error = false;
  errorMessage = '';
  // After first submission attempt, form validation will start
  submitted = false;

  private messages: MessageDetailDto[];

  constructor(
    private messageService: MessageService,
    private notification: ToastrService
  ) {
  }

  ngOnInit() {
    this.loadMessage();
  }

  getMessage(): MessageDetailDto[] {
    return this.messages;
  }

  getText(message: MessageDetailDto): string {
    return "You were invited to drink with " + message.group.name;
  }

  acceptInvitation(message: MessageDetailDto) {
    message.isRead = true;
    return this.messageService.acceptGroupInvitation(message).subscribe({
      next: () => {
        this.notification.success("You joined " + message.group.name);
        this.loadMessage();
      },
      error: error => {
        console.log('Could not accept messages due to:');
        this.defaultServiceErrorHandling(error);
      }
    });
  }

  declineInvitation(message: MessageDetailDto) {
    message.isRead = true;
    return this.messageService.update(message).subscribe({
      next: () => {
        this.notification.success("You declined the group invitation from " + message.group.name);
        this.loadMessage();
      },
      error: error => {
        console.log('Could not decline messages due to:');
        this.defaultServiceErrorHandling(error);
      }
    });
  }

  deleteMessage(message: MessageDetailDto) {
    return this.messageService.deleteById(message.id).subscribe({
      next: () => {
        this.notification.success("You deleted message from " + message.group.name);
        this.loadMessage();
      },
      error: error => {
        console.log('Could not delete messages due to:');
        this.defaultServiceErrorHandling(error);
      }
    });
  }

  /**
   * Loads the specified page of message from the backend
   */
  private loadMessage() {
    this.messageService.getMessage().subscribe({
      next: (messages: MessageDetailDto[]) => {
        this.messages = messages;
      },
      error: error => {
        console.log('Could not load messages due to:');
        this.defaultServiceErrorHandling(error);
      }
    });
  }

  private defaultServiceErrorHandling(error: any) {
    console.log(error);
    this.error = true;
    this.notification.error(error.error.detail);
  }

  /**
   * Error flag will be deactivated, which clears the error message
   */
  vanishError() {
    this.error = false;
  }

}
