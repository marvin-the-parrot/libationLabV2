import {Component, OnInit} from '@angular/core';
import {MessageService} from '../../services/message.service';
import {MessageDetailDto} from "../../dtos/message";

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

  constructor(private messageService: MessageService) {
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

  }

  declineInvitation(message: MessageDetailDto) {
    message.isRead = true;
    return this.messageService.update(message).subscribe({
      next: () => {
        this.loadMessage();
      },
      error: error => {
        this.defaultServiceErrorHandling(error);
      }
    });
  }

  deleteMessage(message: MessageDetailDto) {
    return this.messageService.deleteById(message.id).subscribe({
      next: () => {
        this.loadMessage();
      },
      error: error => {
        this.defaultServiceErrorHandling(error);
      }
    });
  }

  /**
   * Error flag will be deactivated, which clears the error message
   */
  vanishError() {
    this.error = false;
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
        this.defaultServiceErrorHandling(error);
      }
    });
  }

  private defaultServiceErrorHandling(error: any) {
    console.log(error);
    this.error = true;
    if (typeof error.error === 'object') {
      this.errorMessage = error.error.error;
    } else {
      this.errorMessage = error.error;
    }
  }

}
