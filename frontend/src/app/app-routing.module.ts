import {NgModule} from '@angular/core';
import {mapToCanActivate, RouterModule, Routes} from '@angular/router';
import {HomeComponent} from './components/home/home.component';
import {LoginComponent} from './components/login/login.component';
import {AuthGuard} from './guards/auth.guard';
import {MessageComponent} from './components/message/message.component';
import {GroupsComponent} from "./components/groups/groups.component";
import {
  GroupCreateEditComponent,
  GroupCreateEditMode
} from "./components/groups/group-create-edit/group-create-edit.component";
import {GroupDetailComponent} from "./components/groups/group-detail/group-detail.component";
import {CreateAccountComponent} from "./components/create-account/create-account.component";
import {ForgotPasswordComponent} from "./components/forgot-password/forgot-password.component";
import {ResetPasswordComponent} from "./components/reset-password/reset-password.component";
import {UserSettingsComponent} from "./components/user-settings/user-settings.component";

const routes: Routes = [
  {path: '', component: HomeComponent},
  {path: 'login', component: LoginComponent},
  {path: 'register', component: CreateAccountComponent},
  {path: 'forgot-password', component: ForgotPasswordComponent},
  {path: 'reset-password', component: ResetPasswordComponent},
  {path: 'message', canActivate: mapToCanActivate([AuthGuard]), component: MessageComponent},
  {path: 'settings', component: UserSettingsComponent},
  {
    path: 'groups', children: [
      {path: '', component: GroupsComponent},
      {path: 'create', component: GroupCreateEditComponent, data: {mode: GroupCreateEditMode.create}},
      {path: ':id/edit', component: GroupCreateEditComponent, data: {mode: GroupCreateEditMode.edit}},
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
