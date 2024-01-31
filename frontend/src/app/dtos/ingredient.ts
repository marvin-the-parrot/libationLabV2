import {UserListDto} from "./user";
import {CocktailOverviewDto} from "./cocktail";

export interface IngredientGroupDto {
  name: string;
  users: UserListDto[];
}

export interface IngredientListDto {
  id?: number;
  name?: string;
}

export interface IngredientDto {
  id: number;
  name: string;
  amount: string;
}

export interface IngredientSuggestionDto {
  id: number;
  name: string;
  possibleCocktails: CocktailOverviewDto[];
}


