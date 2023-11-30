import {Component} from "@angular/core";
import {ActivatedRoute, Router} from "@angular/router";
import {UserService} from "../../services/user.service";
import {ToastrService} from "ngx-toastr";
import {UntypedFormBuilder, UntypedFormGroup, Validators} from "@angular/forms";
import {ResetPasswordDto} from "../../dtos/resetPassword";

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.scss']
})
export class ResetPasswordComponent {

  resetPasswordForm: UntypedFormGroup;
  // After first submission attempt, form validation will start
  submitted = false;

  token: string = '';

  constructor(
    private formBuilder: UntypedFormBuilder,
    private route: ActivatedRoute,
    private service: UserService,
    private notification: ToastrService,
    private router: Router,
  ) {
    this.resetPasswordForm = this.formBuilder.group({
      newPassword: ['', [Validators.required, Validators.minLength(8)]],
      repeatNewPassword: ['', [Validators.required, Validators.minLength(8)]]
    });
  }


  onSubmit() {
    this.submitted = true;
    if (this.resetPasswordForm.valid) {
      this.checkPasswordsMatch();
    } else {
      console.log('Invalid input');
    }
  }


  checkPasswordsMatch() {
    const newPassword = this.resetPasswordForm.controls.newPassword.value;
    const repeatNewPassword = this.resetPasswordForm.controls.repeatNewPassword.value;

    if (newPassword !== repeatNewPassword) {
      console.log("passwords do not match");
      this.notification.error('Passwords do not match');
    } else {
      const resetPasswordDTO = {
        password: newPassword,
        token: this.token
      };
      this.resetPassword(resetPasswordDTO);
    }
  }



  ngOnInit() {
    // Fetch the token from the URL
    this.route.queryParams.subscribe(params => {
      this.token = params['token'];
    });
  }

  resetPassword(resetPasswordDTO: ResetPasswordDto) {
    // Create a DTO with password and token
    console.log("resetting password")

    // Send the DTO to the backend
    this.service.resetPassword(resetPasswordDTO).subscribe({
      next: () => {
        // Additional logic if needed
        this.notification.success('Password reset successful');
        this.router.navigate(['/login']);
      },
      error: error => {
        // Additional error handling logic
        this.notification.error('Password reset failed: \n' + error.error.detail);
        this.router.navigate(['/login']);
      }
    });
  }
}
