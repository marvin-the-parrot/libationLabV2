import {Component} from "@angular/core";
import {UserService} from "../../services/user.service";

@Component({
    selector: 'app-forgot-password',
    templateUrl: './forgot-password.component.html',
    styleUrls: ['./forgot-password.component.scss']
  })
export class ForgotPasswordComponent {

  email: string = '';

  constructor(private service: UserService) {
  }

  onSubmit() {
    this.forgotPassword(this.email);
  }

  forgotPassword(email: string) {
    this.service.forgotPassword(email).subscribe({
      next: () => {
        console.log("Email sent");
        // Additional logic if needed
      },
      error: error => {
        console.log("Could not send email due to:");
        console.log(error);
        // Additional error handling logic
      }
    });
  }

  ngOnInit() {
  }
}
