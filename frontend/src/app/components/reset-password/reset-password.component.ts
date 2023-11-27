import {Component} from "@angular/core";
import {ActivatedRoute} from "@angular/router";
import {UserService} from "../../services/user.service";

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.scss']
})
export class ResetPasswordComponent {

  newPassword: string = '';
  repeatNewPassword: string = '';
  token: string = '';

  constructor(private route: ActivatedRoute, private service: UserService) {}



  ngOnInit() {
    // Fetch the token from the URL
    this.route.queryParams.subscribe(params => {
      this.token = params['token'];
    });
  }

  resetPassword() {
    // Check if the passwords match and perform encoding
    if (this.newPassword === this.repeatNewPassword) {
      // Create a DTO with password and token
      const resetPasswordDTO = {
        newPassword: this.newPassword,
        token: this.token
      };

      // Send the DTO to the backend
      this.service.sendResetPasswordRequest(resetPasswordDTO);
    } else {
      // Handle password mismatch error
      console.error('Passwords do not match');
    }
  }
}
