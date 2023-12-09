import {Injectable } from '@angular/core';
import {Globals} from '../global/globals';
import {HttpClient} from "@angular/common/http";
import {CocktailListDto} from "../dtos/cocktail";
import {Observable} from "rxjs";
import {environment} from "../../environments/environment";

const baseUri = environment.backendUrl + "/cocktails";

@Injectable({
  providedIn: 'root'
})
export class CocktailService {

  private cocktailBaseUri: string = this.globals.backendUri + '/cocktails';

  constructor(
    private httpClient: HttpClient, 
    private globals: Globals
    ) {
     }

  /**
   * Searching for cocktails 
   * 
   * @param name name of cocktail
   * @returns return matched cocktails
   */
  searchByCoctailName(name: String): Observable<CocktailListDto[]> { 
    return this.httpClient.get<CocktailListDto[]>(`${this.cocktailBaseUri}/searchCocktails/` + name);
  }

   /**
   * Searching for cocktails 
   * 
   * @param name name of ingredient
   * @returns return matched cocktails
   */
    searchByIngredientName(name: String): Observable<CocktailListDto[]> { 
        return this.httpClient.get<CocktailListDto[]>(`${this.cocktailBaseUri}/searchCocktails/ingredient/` + name);
      }

    /**
   * Searching for cocktails 
   * 
   * @param coctail name of cocktail
   * @param ingredient name of ingredient
   * @returns return matched cocktails
   */
    search(coctail: String, ingredient: String): Observable<CocktailListDto[]> { 
        return this.httpClient.get<CocktailListDto[]>(`${this.cocktailBaseUri}/searchCocktails/` + coctail + '/' +ingredient);
    }

}
