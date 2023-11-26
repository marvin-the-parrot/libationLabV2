import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Globals} from '../global/globals';
import {MessageCreateDto, MessageDetailDto} from "../dtos/message";

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
  createMessage(message: MessageCreateDto): Observable<MessageDetailDto> {
    console.log('Create message for user ' + message.userId);
    return this.httpClient.post<MessageDetailDto>(this.messageBaseUri, message);
  }
}
