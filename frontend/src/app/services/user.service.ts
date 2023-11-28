import {HttpClient, HttpParams} from "@angular/common/http";
import {Globals} from "../global/globals";
import {Injectable} from "@angular/core";
import {UserListDto, UserSearch} from "../dtos/user";
import {CreateAccount} from "../dtos/create-account";
import {Observable} from "rxjs";
import {ResetPasswordDto} from "../dtos/resetPassword";

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private baseUri: string = this.globals.backendUri + '/users';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  /**
   * Search for users in the system.
   *
   * @param searchParams the search parameters: username and limit
   * @return an Observable for the list of users
   */
  search(searchParams: UserSearch): Observable<UserListDto[]> {
    if (searchParams.name === '') {
      delete searchParams.name;
    }
    let params = new HttpParams();
    if (searchParams.name) {
      params = params.append('name', searchParams.name);
    }
    if (searchParams.limit) {
      params = params.append('limit', searchParams.limit);
    }
    return this.httpClient.get<UserListDto[]>(this.baseUri, {params});
  }

  /**
   * create a new user
   * @param user the user to create
   * @return an Observable for the created user
   */
  create(user: CreateAccount): Observable<UserListDto> {
    console.log("creating user");
    return this.httpClient.post<UserListDto>(this.baseUri, user);
  }

  /**
   * delete a user
   * @param email the email of the user who wants to reset his password
   * @return an Observable for the send email
   */
  forgotPassword(email: string): Observable<any> {
    return this.httpClient.post<any>(this.baseUri + '/forgot-password', { email });
  }

  /**
   * send a reset password request
   * @param resetPasswordDTO the DTO with the new password and the token
   * @return an Observable for the send request
   */
  resetPassword(resetPasswordDTO: ResetPasswordDto): Observable<ResetPasswordDto> {
    console.log("sending reset password request");
    console.log(resetPasswordDTO.password);
    console.log(resetPasswordDTO.token);
    return this.httpClient.put<ResetPasswordDto>(this.baseUri + '/reset-password', resetPasswordDTO);
  }
}
