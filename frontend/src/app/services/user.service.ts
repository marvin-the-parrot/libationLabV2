import {HttpClient, HttpParams} from "@angular/common/http";
import {Globals} from "../global/globals";
import {Injectable} from "@angular/core";
import {UserListDto} from "../dtos/user";
import {CreateAccount} from "../dtos/create-account";
import {Observable} from "rxjs";

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
   * @return an Observable for the list of users
   */
  public search(name: string): Observable<UserListDto[]> {
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
}
