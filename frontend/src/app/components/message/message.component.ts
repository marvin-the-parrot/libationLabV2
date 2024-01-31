import {Component, OnInit} from '@angular/core';
import {MessageService} from '../../services/message.service';
import {MessageDetailDto, MessageSetReadDto} from "../../dtos/message";
import {ToastrService} from "ngx-toastr";
import {MessageHeaderSharedService} from "../../services/message-header-shared.service";

@Component({
  selector: 'app-message',
  templateUrl: './message.component.html',
  styleUrls: ['./message.component.scss']
})
export class MessageComponent implements OnInit {

  error = false;
  // After first submission attempt, form validation will start
  submitted = false;

  private messages: MessageDetailDto[];
  private messagesToSetRead: MessageSetReadDto[];

  constructor(
    private messageService: MessageService,
    private notification: ToastrService,
    private messageHeaderSharedService: MessageHeaderSharedService
  ) {
  }

  ngOnInit() {
    this.loadMessage();
  }

  getMessage(): MessageDetailDto[] {
    console.log(this.messages);
    return this.messages;
  }

  getUnreadMessageCount(): number {
    return this.messages.filter(message => !message.isRead).length;
  }

  setAllMessagesRead() {
    this.messagesToSetRead = this.messages.map(message => {
      return {id: message.id, isRead: true};
    });

    this.messageService.setAllMessagesRead(this.messagesToSetRead).subscribe({
      next: () => {
        this.notification.success("Successfully set all messages to read");
        this.loadMessage();
      },
      error: error => {
        console.log('Could not set all messages read due to:');
        this.defaultServiceErrorHandling(error);
      }
    });
  }

  setAllMessagesUnread() {
    this.messagesToSetRead = this.messages.map(message => {
      return {id: message.id, isRead: false};
    });

    this.messageService.setAllMessagesRead(this.messagesToSetRead).subscribe({
      next: () => {
        this.notification.success("Successfully set all messages to unread");
        this.loadMessage();
      },
      error: error => {
        console.log('Could not set all messages read due to:');
        this.defaultServiceErrorHandling(error);
      }
    });
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
    this.messageHeaderSharedService.triggerReload();
  }

  private defaultServiceErrorHandling(error: any) {
    console.log(error);
    this.error = true;
    this.notification.error(error.error.detail);
  }

}
