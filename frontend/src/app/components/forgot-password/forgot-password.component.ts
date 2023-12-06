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
    this.service.forgotPassword(email).subscribe({
      next: () => {
        this.notification.info('An Email has been sent to the provided email address');
        this.router.navigate(['/login']);
      },
      error: error => {
        setTimeout(() => {
          this.notification.info('An Email has been sent to the provided email address');
          this.router.navigate(['/login']);
        }, 1000);
      }
    });
  }

  ngOnInit() {
  }
}
