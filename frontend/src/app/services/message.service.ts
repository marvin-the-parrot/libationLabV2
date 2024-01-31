import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Globals} from '../global/globals';
import {MessageCountDto, MessageCreate, MessageDetailDto, MessageSetReadDto} from "../dtos/message";

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
  getUnreadMessageCount(): Observable<MessageCountDto> {
    return this.httpClient.get<MessageCountDto>(this.messageBaseUri + "/count");
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
    return this.httpClient.post<MessageDetailDto>(this.messageBaseUri + '/create', message);
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

  /**
   * Delete a Message by its id.
   *
   * @param messagId the id of the group
   */
  deleteById(messagId: number): Observable<MessageDetailDto> {
    return this.httpClient.delete<MessageDetailDto>(`${this.messageBaseUri}/${messagId}`);
  }

  /**
   * Accept a group invitation.
   *
   * @param message the message to accept
   */
  acceptGroupInvitation(message: MessageDetailDto): Observable<any> {
    return this.httpClient.post<MessageDetailDto>(this.messageBaseUri + '/accept', message);
  }

  /**
   * Set all messages to read.
   */
  setAllMessagesRead(messages: MessageSetReadDto[]): Observable<any> {
    return this.httpClient.put<MessageDetailDto>(this.messageBaseUri + '/read', messages);
  }
}
