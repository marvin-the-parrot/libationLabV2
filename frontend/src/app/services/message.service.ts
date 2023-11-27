import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Globals} from '../global/globals';
import {MessageCreate, MessageDetailDto} from "../dtos/message";
import {GroupOverview} from "../dtos/group-overview";

@Injectable({
  providedIn: 'root'
})
export class MessageService {

  private messageBaseUri: string = this.globals.backendUri + '/messages';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  /**
   * Loads all messages from the backend
   */
  getMessage(): Observable<MessageDetailDto[]> {
    return this.httpClient.get<MessageDetailDto[]>(this.messageBaseUri);
  }

  /**
   * Persists message to the backend
   *
   * @param message to persist
   */
  createMessage(message: MessageCreate): Observable<MessageDetailDto> {
    console.log('Create message for user ' + message.userId);
    return this.httpClient.post<MessageDetailDto>(this.messageBaseUri, message);
  }

  /**
   * Update an existing message in the system.
   *
   * @param message the data for the group that should be updated
   * @return an Observable for the updated group
   */
  update(message: MessageDetailDto): Observable<MessageDetailDto> {
    return this.httpClient.put<MessageDetailDto>(
      this.messageBaseUri + '/' + message.id,
      message
    );
  }
}
