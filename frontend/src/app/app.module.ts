import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {HttpClientModule} from '@angular/common/http';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {HeaderComponent} from './components/header/header.component';
import {FooterComponent} from './components/footer/footer.component';
import {LoginComponent} from './components/login/login.component';
import {MessageComponent} from './components/message/message.component';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {httpInterceptorProviders} from './interceptors';
import { GroupsComponent } from './components/groups/groups.component';
import { GroupThumbComponent } from './components/groups/group-thumb/group-thumb.component';
import { GroupCreateComponent } from './components/groups/group-create/group-create.component';
import { AutocompleteComponent } from './components/autocomplete/autocomplete.component';
import { GroupDetailComponent } from './components/groups/group-detail/group-detail.component';
import { CreateAccountComponent } from './components/create-account/create-account.component';
import {ForgotPasswordComponent} from "./components/forgot-password/forgot-password.component";
import {ResetPasswordComponent} from "./components/reset-password/reset-password.component";
import {ToastrModule} from "ngx-toastr";
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import { ConfirmationDialogComponent } from './confirmation-dialog/confirmation-dialog.component';
import { MatDialogModule } from '@angular/material/dialog';
import { OptionDialogComponent } from './option-dialog/option-dialog.component';
import { GroupEditComponent } from './components/groups/group-edit/group-edit.component';
import { UserSettingsComponent } from './components/user-settings/user-settings.component';


@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    FooterComponent,
    LoginComponent,
    MessageComponent,
    GroupsComponent,
    GroupThumbComponent,
    GroupCreateComponent,
    AutocompleteComponent,
    GroupDetailComponent,
    CreateAccountComponent,
    ForgotPasswordComponent,
    ResetPasswordComponent,
    ConfirmationDialogComponent,
    OptionDialogComponent,
    GroupEditComponent,
    UserSettingsComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule,
    HttpClientModule,
    NgbModule,
    FormsModule,
    ToastrModule.forRoot(),
    BrowserAnimationsModule,
    MatDialogModule,
  ],
  providers: [httpInterceptorProviders],
  bootstrap: [AppComponent]
})
export class AppModule {
}
