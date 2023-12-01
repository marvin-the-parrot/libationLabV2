import {HttpClient} from "@angular/common/http";
import {Globals} from "../global/globals";
import {GroupOverview} from "../dtos/group-overview";
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
   * Gets a members by name and group id.
   *
   * @param groupId the id of the group
   * @param name the name of member
   */
  getByName(groupId: number, name: string): Observable<GroupOverview> {
    return this.httpClient.get<GroupOverview>(`${this.baseUri}/${groupId}/${name}`);
  }

  /**
   * Delete a GroupOverview by its id.
   *
   * @param groupId the id of the group
   * @param hostId the id of host
   */
  deleteById(groupId: number, hostId: number): Observable<void> {
    return new Observable<void>((observer) => {
      this.httpClient.delete<GroupOverview>(`${this.baseUri}/${groupId}/${hostId}`).subscribe(
        () => {
          observer.complete();
        },
        (error) => {
          console.error('Error deleting:', error);
          observer.error(error);
        }
      );
    });
  }

  /**
   * Delete a member of GroupOverview by its id.
   *
   * @param id the id of the group
   * @param memberId the id of member to delete
   * @param hostId the id of host
   */
  deleteByMemberByIdAndGroupId(groupId: number, memberId: number, hostId: number): void {
    this.httpClient.delete<GroupOverview>(`${this.baseUri}/${groupId}/${memberId}/${hostId}`);
  }

  getAllByUser() {
    return this.httpClient.get<GroupOverview[]>(`${this.baseUri}`);
  }
}
