import {Component} from "@angular/core";
import {UserService} from "../../services/user.service";
import {Router} from "@angular/router";
import {ToastrService} from 'ngx-toastr';


@Component({
    selector: 'app-forgot-password',
    templateUrl: './forgot-password.component.html',
    styleUrls: ['./forgot-password.component.scss']
  })
export class ForgotPasswordComponent {

  email: string = '';

  constructor(
    private service: UserService,
    private router: Router,
    private notification: ToastrService
    ) {
  }

  onSubmit() {
    this.forgotPassword(this.email);
  }

  forgotPassword(email: string) {
    console.log(email);
    this.service.forgotPassword(email).subscribe({
      next: () => {
        console.log("Email sent");
        // Additional logic if needed
        this.notification.success('Email sent to: \n' + email);
        this.router.navigate(['/login']);
      },
      error: error => {
        console.log("Could not send email due to:");
        console.log(error);
        // Additional error handling logic
        this.notification.error('Could not send email due to: \n' + error.error.detail);
      }
    });
  }

  ngOnInit() {
  }
}
