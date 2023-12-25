import {Injectable } from '@angular/core';
import {Globals} from '../global/globals';
import {HttpClient} from "@angular/common/http";
import {CocktailListDto} from "../dtos/cocktail";
import {Observable} from "rxjs";
import {environment} from "../../environments/environment";
import { MenuCocktailsDto } from '../dtos/menu';

const baseUri = environment.backendUrl + "/cocktails";

@Injectable({
  providedIn: 'root'
})
export class CocktailService {

  private cocktailBaseUri: string = this.globals.backendUri + '/cocktails';
  private menuBaseUri: string = this.globals.backendUri + '/menu';

  constructor(
    private httpClient: HttpClient, 
    private globals: Globals
    ) {
     }

  searchCocktails(cocktailName: String, ingredient: String, preference: String): Observable<CocktailListDto[]> {
    if ((cocktailName == null || cocktailName.length == 0) && (preference  == null || preference.length == 0) && ingredient) {
      return this.searchByIngredientName(ingredient);
    } else if ((ingredient == null || ingredient.length == 0) && (preference == null || preference.length == 0) && cocktailName) {
      return this.searchByCoctailName(cocktailName);
    } else if ((preference == null || preference.length == 0) && cocktailName && ingredient){
      return this.search(cocktailName, ingredient);
    } else if ((cocktailName == null || cocktailName.length == 0) && (ingredient == null || ingredient.length == 0) && preference) {
      return this.searchByPreferenceName(preference);
    } else if ((cocktailName == null || cocktailName.length == 0) && ingredient && preference) {
      return this.searchByIngredientNameAndPreference(ingredient, preference);
    } else if ((ingredient == null || ingredient.length == 0) && cocktailName && preference) {
      return this.searchByCoctailNameAndPreference(cocktailName, preference); 
    } else {
      return this.searchByCocktailNameAndIngredientAndPreference(cocktailName, ingredient, preference);
    }
  }

  /**
   * Searching for cocktails by cocktail name
   * 
   * @param name name of cocktail
   * @returns return matched cocktails
   */
  searchByCoctailName(name: String): Observable<CocktailListDto[]> { 
    return this.httpClient.get<CocktailListDto[]>(`${this.cocktailBaseUri}/searchCocktails/cocktail/` + name);
  }

  /**
   * Searching for cocktails by coctail name and preference
   * 
   * @param name name of cocktail
   * @param preference name of preference
   * @returns return matched cocktails
   */
    searchByCoctailNameAndPreference(name: String, preference: String): Observable<CocktailListDto[]> { 
      return this.httpClient.get<CocktailListDto[]>(`${this.cocktailBaseUri}/searchCocktails/cocktail/` + name + '/' + preference);
    }

   /**
   * Searching for cocktails by ingredient name
   * 
   * @param name name of ingredient
   * @returns return matched cocktails
   */
    searchByIngredientName(name: String): Observable<CocktailListDto[]> { 
        return this.httpClient.get<CocktailListDto[]>(`${this.cocktailBaseUri}/searchCocktails/ingredient/` + name);
    }

   /**
   * Searching for cocktails by ingredient name and preference
   * 
   * @param name name of ingredient
   * @param preference name of preference
   * @returns return matched cocktails
   */
    searchByIngredientNameAndPreference(name: String, preference: String): Observable<CocktailListDto[]> { 
        return this.httpClient.get<CocktailListDto[]>(`${this.cocktailBaseUri}/searchCocktails/ingredient/` + name + '/' + preference);
    }

    /**
   * Searching for cocktails by preference name
   * 
   * @param name name of preference
   * @returns return matched cocktails
   */
    searchByPreferenceName(name: String): Observable<CocktailListDto[]> { 
      return this.httpClient.get<CocktailListDto[]>(`${this.cocktailBaseUri}/searchCocktails/preference/` + name);
    }

  /**
   * Searching for cocktails 
   * 
   * @param coctail name of cocktail
   * @param ingredient name of ingredient
   * @returns return matched cocktails
   */
    search(coctail: String, ingredient: String): Observable<CocktailListDto[]> { 
        return this.httpClient.get<CocktailListDto[]>(`${this.cocktailBaseUri}/searchCocktails/` + coctail + '/' + ingredient);
    }

  /**
   * Searching for cocktails by cocktail name and ingredient and preference
   * 
   * @param coctail name of cocktail
   * @param ingredient name of ingredient
   * @param preference name of preference
   * @returns return matched cocktails
   */
  searchByCocktailNameAndIngredientAndPreference(coctail: String, ingredient: String, preference): Observable<CocktailListDto[]> { 
      return this.httpClient.get<CocktailListDto[]>(`${this.cocktailBaseUri}/searchCocktails/` + coctail + '/' + ingredient + '/' + preference);
  }

  /**
     * Adding cocktails menu
     * 
     * @param cocktails list of cocktails 
     * @returns saved cocktails menu
     */
  saveCocktails(cocktails: MenuCocktailsDto): Observable<MenuCocktailsDto> {
    return this.httpClient.post<MenuCocktailsDto>(this.menuBaseUri, cocktails);
}

    /**
     * Get cocktail menu of a group 
     * 
     */
    getCocktailMenu(groupId: number): Observable<MenuCocktailsDto> {
      return this.httpClient.get<MenuCocktailsDto>(this.menuBaseUri + `/${groupId}`);
  }

}
