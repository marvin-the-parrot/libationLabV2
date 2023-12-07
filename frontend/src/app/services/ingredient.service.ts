import { Injectable } from '@angular/core';
import {Globals} from '../global/globals';
import {HttpClient, HttpParams} from "@angular/common/http";
import {IngredientListDto, IngredientGroupDto} from "../dtos/ingredient";
import {Observable} from "rxjs";
import {UserListDto} from "../dtos/user";

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

  /**
   * Loads all fitting ingredients for autocomplete
   */
  public searchIngredientsUserExisting(name: string, id: number): Observable<IngredientListDto[]> {
    let params = new HttpParams();
    params = params.append("name", name);
    params = params.append("userId", id);

    return this.httpClient.get<IngredientListDto[]>(this.ingredientBaseUri + '/user-ingredients-auto', { params });
  }

  /**
   * Loads all ingredients of a user from the backend
   */
  getUserIngredients(): Observable<IngredientListDto[]> {
    return this.httpClient.get<IngredientListDto[]>(this.ingredientBaseUri + '/user-ingredients');
  }

  /**
   * Adds an ingredient to the backend wich a user had set
   */
  saveUserIngredients(ingredients: IngredientListDto[]): Observable<IngredientListDto[]> {
    return this.httpClient.post<IngredientListDto[]>(this.ingredientBaseUri + '/user-ingredients', ingredients);
  }

}
