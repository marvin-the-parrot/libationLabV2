import {HttpClient, HttpParams} from "@angular/common/http";
import {Globals} from "../global/globals";
import {Injectable} from "@angular/core";
import {UserListDto, UserSearch} from "../dtos/user";
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
}
