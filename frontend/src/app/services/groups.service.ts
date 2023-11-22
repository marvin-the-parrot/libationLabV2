import {HttpClient} from "@angular/common/http";
import {Globals} from "../global/globals";
import {Group} from "../dtos/group";
import {Observable} from "rxjs";
import {Injectable} from "@angular/core";

@Injectable({
  providedIn: 'root'
})
export class GroupsService {

  private baseUri: string = this.globals.backendUri + '/groups';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  /**
   * Create a new group in the system.
   *
   * @param group the data for the group that should be created
   * @return an Observable for the created group
   */
  create(group: Group): Observable<Group> {
    return this.httpClient.post<Group>(
      this.baseUri,
      group
    );
  }


  /**
   * Update an existing group in the system.
   *
   * @param group the data for the group that should be updated
   * @return an Observable for the updated group
   */
  update(group: Group): Observable<Group> {
    return this.httpClient.put<Group>(
      this.baseUri + '/' + group.id,
      group
    );
  }

  /**
   * Gets a Group by its id.
   *
   * @param id the id of the group
   */
  getById(id: number): Observable<Group> {
    return this.httpClient.get<Group>(`${this.baseUri}/${id}`);
  }
}
