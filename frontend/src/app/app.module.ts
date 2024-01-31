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
import { IngredientComponent } from './components/ingredient/ingredient.component';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import {MatCheckboxModule} from "@angular/material/checkbox";
import { CocktailComponent } from './components/cocktail/cocktail.component';
import { CocktailMenuComponent } from './components/cocktail/cocktail-menu/cocktail-menu.component';
import { AddIngredientDialogComponent } from './add-ingredient-dialog/add-ingredient-dialog.component';
import {NgOptimizedImage} from "@angular/common";
import { CocktailDetailComponent } from './components/cocktail/cocktail-detail/cocktail-detail.component';
import {MatChipsModule} from "@angular/material/chips";
import { GenerateMenuDialogComponent } from './generate-menu-dialog/generate-menu-dialog.component';
import { CocktailDetailPageComponent } from './components/cocktail/cocktail-detail/cocktail-detail-page/cocktail-detail-page.component';


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
    IngredientComponent,
    CocktailComponent,
    CocktailMenuComponent,
    AddIngredientDialogComponent,
    CocktailDetailComponent,
    GenerateMenuDialogComponent,
    CocktailDetailPageComponent,
  ],
  imports: [
    MatIconModule,
    MatInputModule,
    BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule,
    HttpClientModule,
    NgbModule,
    FormsModule,
    ToastrModule.forRoot(),
    BrowserAnimationsModule,
    MatDialogModule,
    MatCheckboxModule,
    NgOptimizedImage,
    MatChipsModule,
  ],
  providers: [httpInterceptorProviders],
  bootstrap: [AppComponent]
})
export class AppModule {
}
