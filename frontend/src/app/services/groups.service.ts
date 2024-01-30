import {HttpClient} from "@angular/common/http";
import {Globals} from "../global/globals";
import {GroupOverview} from "../dtos/group-overview";
import {Observable} from "rxjs";
import {Injectable} from "@angular/core";
import {CocktailDetailDto} from "../dtos/cocktail";

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
  create(group: GroupOverview): Observable<GroupOverview> {
    return this.httpClient.post<GroupOverview>(
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
  update(group: GroupOverview): Observable<GroupOverview> {
    return this.httpClient.put<GroupOverview>(
      this.baseUri + '/' + group.id,
      group
    );
  }

  /**
   * Gets a GroupOverview by its id.
   *
   * @param id the id of the group
   */
  getById(id: number): Observable<GroupOverview> {
    return this.httpClient.get<GroupOverview>(`${this.baseUri}/${id}`);
  }

  /**
   * Delete a GroupOverview by its id.
   *
   * @param groupId the id of the group
   */
  deleteGroup(groupId: number): Observable<GroupOverview> {
    return this.httpClient.delete<GroupOverview>(`${this.baseUri}/${groupId}`);
  }

  /**
   * Delete a member of GroupOverview by its id.
   *
   * @param groupId the id of the group
   * @param memberId the id of member to delete
   */
  removeMemberFromGroup(groupId: number, memberId: number): Observable<GroupOverview> {
    return this.httpClient.delete<GroupOverview>(this.baseUri + '/' + groupId + '/' + memberId);
  }

  /**
   * Make a member the host of a group.
   *
   * @param groupId the id of the group
   * @param memberId the id of member to make host
   */
  makeMemberHost(groupId: number, memberId: number): Observable<GroupOverview> {
    return this.httpClient.put<GroupOverview>(this.baseUri + '/' + groupId + '/' + memberId, null);
  }

  getAllByUser() {
    return this.httpClient.get<GroupOverview[]>(`${this.baseUri}`);
  }

  /**
   * Get all mixable cocktails for a group.
   * @param id
   */
  getMixables(id: number) {
    return this.httpClient.get<CocktailDetailDto[]>(`${this.baseUri}/${id}/mixables`);
  }
}
