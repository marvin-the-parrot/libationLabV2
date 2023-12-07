import { Injectable } from '@angular/core';
import {Globals} from '../global/globals';
import {HttpClient, HttpParams} from "@angular/common/http";
import {IngredientGroupDto} from "../dtos/ingredient";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class IngredientService {

  private ingredientBaseUri: string = this.globals.backendUri + '/ingredients';

  constructor(private httpClient: HttpClient, private globals: Globals) { }

  /**
   * Loads all ingredients of a group from the backend
   */
  getAllGroupIngredients(groupId: number): Observable<IngredientGroupDto[]> {
    let params = new HttpParams();
    params = params.append("groupId", groupId);

    return this.httpClient.get<IngredientGroupDto[]>(this.ingredientBaseUri, { params });
  }
}
