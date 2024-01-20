import {Injectable} from '@angular/core';
import {Globals} from '../global/globals';
import {HttpClient, HttpParams} from "@angular/common/http";
import {CocktailDetailDto, CocktailListDto, CocktailTagSearchDto} from "../dtos/cocktail";
import {Observable} from "rxjs";
import {MenuCocktailsDetailViewDto, MenuCocktailsDto} from '../dtos/menu';
import {IngredientListDto} from "../dtos/ingredient";
import {PreferenceListDto} from "../dtos/preference";
import {RecommendedMenues} from "../dtos/recommendedMenues";

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

  searchCocktails(searchParams: CocktailTagSearchDto): Observable<CocktailDetailDto[]> {
    if (searchParams.cocktailName === '') {
      delete searchParams.cocktailName;
    }
    let params = new HttpParams();
    if (searchParams.cocktailName) {
      params = params.append('cocktailName', searchParams.cocktailName);
    }
    if (searchParams.selectedIngredients.length > 0) {
      params = params.append('ingredientsName', searchParams.selectedIngredients.join(','));
    }
    if (searchParams.selectedPreferences.length > 0) {
      params = params.append('preferenceName', searchParams.selectedPreferences.join(','));
    }
    return this.httpClient.get<CocktailDetailDto[]>(this.cocktailBaseUri, {params});
  }

  getCocktailById(id: number): Observable<CocktailDetailDto> {
    return this.httpClient.get<CocktailDetailDto>(`${this.cocktailBaseUri}/${id}`);
  }

  /*searchCocktails(cocktailName: String, ingredient: String, preference: String): Observable<CocktailListDto[]> {
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
}*/

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
   * @param cocktail name of cocktail
   * @param ingredient name of ingredient
   * @returns return matched cocktails
   */
  search(cocktail: String, ingredient: String): Observable<CocktailListDto[]> {
    return this.httpClient.get<CocktailListDto[]>(`${this.cocktailBaseUri}/searchCocktails/` + cocktail + '/' + ingredient);
  }

  /**
   * Searching for cocktails by cocktail name and ingredient and preference
   *
   * @param cocktail name of cocktail
   * @param ingredient name of ingredient
   * @param preference name of preference
   * @returns return matched cocktails
   */
  searchByCocktailNameAndIngredientAndPreference(cocktail: String, ingredient: String, preference): Observable<CocktailListDto[]> {
    return this.httpClient.get<CocktailListDto[]>(`${this.cocktailBaseUri}/searchCocktails/` + cocktail + '/' + ingredient + '/' + preference);
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


  /**
   * Update cocktail menu of groups from a user
   * when changing ingredients
   *
   */
  updateCocktailMenu(userIngredients: IngredientListDto[]): Observable<MenuCocktailsDto> {
    return this.httpClient.put<MenuCocktailsDto>(this.menuBaseUri, userIngredients);
  }

  /**
   * Loads all fitting ingredients for autocomplete
   */
  searchIngredientsAuto(name: string): Observable<IngredientListDto[]> {
    return this.httpClient.get<IngredientListDto[]>(this.cocktailBaseUri + '/cocktail-ingredients-auto/' + name);
  }

  /**
   * Loads all fitting preferences for autocomplete
   */
  searchPreferencesAuto(name: string): Observable<PreferenceListDto[]> {
    return this.httpClient.get<PreferenceListDto[]>(this.cocktailBaseUri + '/cocktail-preferences-auto/' + name);
  }

  /**
   * Generates a selection of 3 cocktail menus for a group
   * @param groupId
   * @param numberOfCocktails
   */
  generateCocktailMenu(groupId: number, numberOfCocktails: number): Observable<RecommendedMenues> {
    let params = new HttpParams();
    params = params.append("numberOfCocktails", numberOfCocktails.toString());

    return this.httpClient.get<RecommendedMenues>(this.menuBaseUri + '/' + groupId + '/recommendation', {params});
  }

  /**
   * Get cocktail menu of a group with rating of user for GroupDetailView
   *
   */
  getCocktailMenuDetailView(groupId: number, cocktailId: number): Observable<MenuCocktailsDetailViewDto> {
      let params = new HttpParams();
      params = params.append("groupId", groupId.toString());
      params = params.append("cocktailId", cocktailId.toString());

      return this.httpClient.get<MenuCocktailsDetailViewDto>(this.menuBaseUri + `/${groupId}/detail`, {params});
  }
}
