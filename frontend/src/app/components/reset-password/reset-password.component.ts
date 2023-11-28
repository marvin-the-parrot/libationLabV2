import {Component} from "@angular/core";
import {ActivatedRoute, Router} from "@angular/router";
import {UserService} from "../../services/user.service";
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.scss']
})
export class ResetPasswordComponent {

  newPassword: string = '';
  repeatNewPassword: string = '';
  token: string = '';

  constructor(
    private route: ActivatedRoute,
    private service: UserService,
    private notification: ToastrService,
    private router: Router,
  ) {}



  ngOnInit() {
    // Fetch the token from the URL
    this.route.queryParams.subscribe(params => {
      this.token = params['token'];
    });
  }

  resetPassword() {
    // Check if the passwords match and perform encoding
    console.log(this.token);
    if (this.newPassword === this.repeatNewPassword) {
      // Create a DTO with password and token
      console.log("resetting password")
      const resetPasswordDTO = {
        password: this.newPassword,
        token: this.token
      };

      // Send the DTO to the backend
      this.service.resetPassword(resetPasswordDTO).subscribe({
        next: () => {
          // Additional logic if needed
          this.notification.success('Password reset successful');
          this.router.navigate(['/login']);
        },
        error: error => {
          // Additional error handling logic
          this.notification.error('Password reset failed: ' + error.error.message);
          this.router.navigate(['/login']);
        }
      });
    } else {
      // Handle password mismatch error
      this.notification.error('Passwords do not match');
    }
  }
}
