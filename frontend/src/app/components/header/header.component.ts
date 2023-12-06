import {Component, OnInit} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {MessageCountDto} from "../../dtos/message";
import {MessageService} from "../../services/message.service";
import {ToastrService} from "ngx-toastr";
import {MessageHeaderSharedService} from "../../services/message-header-shared.service";
import {Subscription} from "rxjs";

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {

  error = false;
  private message: MessageCountDto;
  private subscription: Subscription;

  constructor(
    public authService: AuthService,
    private messageService: MessageService,
    private notification: ToastrService,
    private messageHeaderSharedService: MessageHeaderSharedService
  ) {
    this.subscription = this.messageHeaderSharedService.reload$.subscribe(() => {
      this.loadMessage();
    });
  }

  ngOnInit() {
    this.loadMessage();
  }

  protected readonly username = JSON.parse(localStorage.getItem('user')).name;

  public getUnreadMessageCount(): number {
    if (this.message == null) {
      return 0;
    }
    return this.message.count;
  }

  /**
   * Loads the specified page of message from the backend
   */
  private loadMessage() {
    this.messageService.getUnreadMessageCount().subscribe({
      next: (message: MessageCountDto) => {
        this.message = message;
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
}
