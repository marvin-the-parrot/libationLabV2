import {IngredientDto} from "./ingredient";
import { List } from 'immutable'; // Import List from Immutable.js

export interface CocktailListDto {
  id: number;
  name: string;
  imagePath: string;
}

export interface CocktailOverviewDto {
  id: number;
  name: string;
  imagePath: string;
  ingredients: IngredientDto[];
  instructions: string;
}

export interface CocktailDetailDto {
  id: number;
  name: string;
  imagePath: string;
  ingredients: Map<string, string>;
  preferenceName: string[];
  instructions: string;
}

export interface CocktailSearch {
  cocktailName?: string;
  ingredientsName?: string;
  preferenceName?: string;
}

export interface CocktailTagSearchDto {
  cocktailName?: string;
  selectedIngredients?: string[];
  selectedPreferences?: string[];
}


