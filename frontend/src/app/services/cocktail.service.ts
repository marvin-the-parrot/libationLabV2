import {Injectable} from '@angular/core';
import {Globals} from '../global/globals';
import {HttpClient, HttpParams} from "@angular/common/http";
import {CocktailDetailDto, CocktailTagSearchDto} from "../dtos/cocktail";
import {Observable} from "rxjs";
import {MenuCocktailsDetailViewDto, MenuCocktailsDetailViewHostDto, MenuCocktailsDto} from '../dtos/menu';
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
  getCocktailMenuDetailView(groupId: number): Observable<MenuCocktailsDetailViewDto> {
      return this.httpClient.get<MenuCocktailsDetailViewDto>(this.menuBaseUri + `/${groupId}/detail`);
  }

  /**
   * Get cocktail menu of a group with rating of user for GroupDetailView
   *
   */
  getCocktailMenuDetailViewHost(groupId: number): Observable<MenuCocktailsDetailViewHostDto> {
    return this.httpClient.get<MenuCocktailsDetailViewHostDto>(this.menuBaseUri + `/${groupId}/detail/host`);
  }
}
