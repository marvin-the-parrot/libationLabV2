import {Injectable} from '@angular/core';
import {Globals} from '../global/globals';
import {HttpClient, HttpParams} from "@angular/common/http";
import {IngredientGroupDto, IngredientListDto, IngredientSuggestionDto} from "../dtos/ingredient";
import {Observable, switchMap} from "rxjs";
import {environment} from "../../environments/environment";

const baseUri = environment.backendUrl + "/ingredients";

@Injectable({
    providedIn: 'root'
})
export class IngredientService {

    private ingredientBaseUri: string = this.globals.backendUri + '/ingredients';

    constructor(
        private httpClient: HttpClient,
        private globals: Globals
    ) {
    }

    /**
     * Loads all ingredients of a group from the backend
     */
    getAllGroupIngredients(groupId: number): Observable<IngredientGroupDto[]> {
        let params = new HttpParams();
        params = params.append("groupId", groupId);

        return this.httpClient.get<IngredientGroupDto[]>(this.ingredientBaseUri + `/${groupId}`);
    }

    /**
     * Returns ingredients suggestions for a group
     *
     * @param groupId The id of the group to get suggestions for
     */
    getIngredientSuggestions(groupId: number): Observable<IngredientSuggestionDto[]> {
        return this.httpClient.get<IngredientSuggestionDto[]>(this.ingredientBaseUri + `/suggestions/${groupId}`);
    }


    /**
     * Adds an ingredient to the current user's ingredients
     *
     * @param ingredient The ingredient to add
     */
    addIngredientSuggestion(ingredient: IngredientListDto): Observable<IngredientListDto[]> {
        return this.getUserIngredients().pipe(
            switchMap((ingredients: IngredientListDto[]) => {
                ingredients.push(ingredient);
                return this.saveUserIngredients(ingredients);
            })
        );
    }

    /**
     * Loads all fitting ingredients for autocomplete
     */
    public searchIngredientsUserExisting(name: string, id: number): Observable<IngredientListDto[]> {
        let params = new HttpParams();
        params = params.append("name", name);

        return this.httpClient.get<IngredientListDto[]>(this.ingredientBaseUri + '/user-ingredients-auto/' + name);
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

    /**
     * Searching for ingredients
     *
     * @param name of ingredient
     * @returns return matched ingredients
     */
    search(name: String): Observable<IngredientListDto[]> {
        return this.httpClient.get<IngredientListDto[]>(`${this.ingredientBaseUri}/searchIngredients/` + name);
    }

}
