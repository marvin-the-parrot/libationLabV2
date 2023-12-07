import {NgModule} from '@angular/core';
import {mapToCanActivate, RouterModule, Routes} from '@angular/router';
import {LoginComponent} from './components/login/login.component';
import {AuthGuard} from './guards/auth.guard';
import {MessageComponent} from './components/message/message.component';
import {GroupsComponent} from "./components/groups/groups.component";
import {GroupDetailComponent} from "./components/groups/group-detail/group-detail.component";
import {CreateAccountComponent} from "./components/create-account/create-account.component";
import {ForgotPasswordComponent} from "./components/forgot-password/forgot-password.component";
import {ResetPasswordComponent} from "./components/reset-password/reset-password.component";
import {UserSettingsComponent} from "./components/user-settings/user-settings.component";
import {GroupEditComponent} from "./components/groups/group-edit/group-edit.component";
import {GroupCreateComponent} from "./components/groups/group-create/group-create.component";

const routes: Routes = [
  {path: '', pathMatch: 'full', redirectTo: 'groups'},
  {path: 'login', component: LoginComponent},
  {path: 'register', component: CreateAccountComponent},
  {path: 'forgot-password', component: ForgotPasswordComponent},
  {path: 'reset-password', component: ResetPasswordComponent},
  {path: 'message', canActivate: mapToCanActivate([AuthGuard]), component: MessageComponent},
  {path: 'settings', canActivate: mapToCanActivate([AuthGuard]), component: UserSettingsComponent},
  {
    path: 'groups',canActivate: mapToCanActivate([AuthGuard]), children: [
      {path: '', component: GroupsComponent},
      {path: 'create', component: GroupCreateComponent},
      {path: ':id/edit', component: GroupEditComponent},
      {path: ':id/detail', component: GroupDetailComponent}
    ]
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {useHash: true})],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
