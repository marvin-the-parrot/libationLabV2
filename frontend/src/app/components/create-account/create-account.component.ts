import {Component, OnInit} from '@angular/core';
import {UntypedFormBuilder, UntypedFormGroup, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {CreateAccount} from "../../dtos/create-account";
import {UserService} from "../../services/user.service";


@Component({
  selector: 'app-create-account',
  templateUrl: './create-account.component.html',
  styleUrls: ['./create-account.component.scss']
})
export class CreateAccountComponent implements OnInit {

  createAccountForm: UntypedFormGroup;
  // After first submission attempt, form validation will start
  submitted = false;
  // Error flag
  error = false;
  errorMessage = '';

  constructor(private formBuilder: UntypedFormBuilder, private userService: UserService, private router: Router) {
    this.createAccountForm = this.formBuilder.group({
      username: ['', [Validators.required]],
      email: ['', [Validators.required]],
      password: ['', [Validators.required, Validators.minLength(8)]]
    });
  }

  /**
   * Form validation will start after the method is called, additionally an AuthRequest will be sent
   */
  createUser() {
    this.submitted = true;
    if (this.createAccountForm.valid) {
      const createAccount: CreateAccount = new CreateAccount(this.createAccountForm.controls.username.value, this.createAccountForm.controls.password.value, this.createAccountForm.controls.email.value);
      this.create(createAccount);
    } else {
      console.log('Invalid input');
    }
  }

  /**
   * Send Creat data to the backend if it was successfully, the user will be forwarded
   *
   * @param createAccount create account data from the form
   */
  create(createAccount: CreateAccount) {
    this.userService.create(createAccount).subscribe({
      next: () => {
        this.router.navigate(['/login']);
      },
      error: error => {
        console.log('Could not create account due to:');
        console.log(error);
        this.error = true;
        if (typeof error.error === 'object') {
          this.errorMessage = error.error.error;
        } else {
          this.errorMessage = error.error;
        }
      }
    });
  }

  /**
   * Error flag will be deactivated, which clears the error message
   */
  vanishError() {
    this.error = false;
  }

  ngOnInit() {
  }

}
