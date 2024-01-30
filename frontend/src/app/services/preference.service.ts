import {Injectable} from "@angular/core";
import {Globals} from '../global/globals';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {PreferenceListDto} from "../dtos/preference";

@Injectable({
  providedIn: 'root'
})
export class PreferenceService {

  private preferenceBaseUri: string = this.globals.backendUri + '/preferences';

  constructor(
    private httpClient: HttpClient,
    private globals: Globals
  ) {
  }


  /**
   * Loads all fitting preferences for autocomplete
   */
  searchPreferencesUserExisting(name: string): Observable<PreferenceListDto[]> {
    return this.httpClient.get<PreferenceListDto[]>(this.preferenceBaseUri + '/user-preference-auto/' + name);
  }

  /**
   * Loads all preferences of a user from the backend
   */
  getUserPreferences(): Observable<PreferenceListDto[]> {
    return this.httpClient.get<PreferenceListDto[]>(this.preferenceBaseUri + '/user-preferences');
  }

  /**
   * Adds preferences to the backend which the user had set
   */
  saveUserPreferences(preferences: PreferenceListDto[]): Observable<PreferenceListDto[]> {
    return this.httpClient.post<PreferenceListDto[]>(this.preferenceBaseUri + '/user-preferences', preferences);
  }







}
