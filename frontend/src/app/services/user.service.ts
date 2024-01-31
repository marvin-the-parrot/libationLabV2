import {HttpClient, HttpParams} from "@angular/common/http";
import {Globals} from "../global/globals";
import {Injectable} from "@angular/core";
import {UserListDto, UserLocalStorageDto} from "../dtos/user";
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
   * @param name the search parameters: username and limit
   * @param id the search parameters: username and limit
   * @return an Observable for the list of users
   */
  public searchUsersGroupExisting(name: string, id: number): Observable<UserListDto[]> {
    let params = new HttpParams();
    params = params.append("name", name);
    params = params.append("groupId", id);

    return this.httpClient.get<UserListDto[]>(this.baseUri, { params });
  }

  /**
   * Search for users in the system.
   *
   * @param name the search parameters: username and limit
   * @param members
   * @return an Observable for the list of users
   */
  public searchUsersGroupCreating(name: string, members: UserListDto[]): Observable<UserListDto[]> {
    let params = new HttpParams();
    params = params.append("name", name);

    return this.httpClient.get<UserListDto[]>(this.baseUri, { params });
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
    return this.httpClient.put<ResetPasswordDto>(this.baseUri + '/reset-password', resetPasswordDTO);
  }



  getUser(): Observable<UserLocalStorageDto> {
    return this.httpClient.get<UserLocalStorageDto>(this.baseUri + '/user');
  }

  deleteUser(): Observable<any> {
    return this.httpClient.delete<any>(this.baseUri + '/delete');
  }
}
