import {Component, OnInit} from '@angular/core';
import {UntypedFormBuilder, UntypedFormGroup, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {AuthService} from '../../services/auth.service';
import {AuthRequest} from '../../dtos/auth-request';
import {ToastrService} from "ngx-toastr";
import {UserService} from "../../services/user.service";


@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  loginForm: UntypedFormGroup;
  // After first submission attempt, form validation will start
  submitted = false;

  constructor(
    private formBuilder: UntypedFormBuilder,
    private authService: AuthService,
    private userService: UserService,
    private router: Router,
    private notification: ToastrService
    ) {
    this.loginForm = this.formBuilder.group({
      username: ['', [Validators.required]],
      password: ['', [Validators.required, Validators.minLength(8)]]
    });
  }

  /**
   * Form validation will start after the method is called, additionally an AuthRequest will be sent
   */
  loginUser() {
    this.submitted = true;
    if (this.loginForm.valid) {
      const authRequest: AuthRequest = new AuthRequest(this.loginForm.controls.username.value, this.loginForm.controls.password.value);
      this.authenticateUser(authRequest);
    } else {
      console.log('Invalid input');
    }
  }

  /**
   * Send authentication data to the authService. If the authentication was successfully, the user will be forwarded to the message page
   *
   * @param authRequest authentication data from the user login form
   */
  authenticateUser(authRequest: AuthRequest) {
    console.log('Try to authenticate user: ' + authRequest.email);
    this.authService.loginUser(authRequest).subscribe({
      next: () => {
        console.log('Successfully logged in user: ' + authRequest.email);
        this.notification.success('Successfully logged in user: \n' + authRequest.email);
        this.setUsernameLocalStorage();
        this.setUserLocalStorage();
        this.router.navigate(['/groups']);
      },
      error: error => {
        console.log('Could not log in due to:');
        console.log(error);
        if (error.status==401) {
          this.notification.error('Unrecognised combination of username and password');
        }else{
          this.notification.error('Error during Log in:');
        }
      }
    });
  }

  ngOnInit() {
  }

  /**
   * Get Username from backend and return it
   */
  // todo: remove this method (as there is one that saves username and user id in the local storage)
  setUsernameLocalStorage() {
    this.userService.getUsername().subscribe({
      next: data => {
        console.log(data);
        localStorage.setItem('username', data.username);
        console.log(localStorage.getItem('username'));
      },
      error: error => {
        console.log(error);
      }
    });
  }

  /**
   * Get User from backend and return it
   */
  setUserLocalStorage() {
    this.userService.getUser().subscribe({
      next: data => {
        console.log(data);
        localStorage.setItem('user', JSON.stringify(data));
        console.log(localStorage.getItem('user'));
      },
      error: error => {
        console.log(error);
      }
    });
  }

}
