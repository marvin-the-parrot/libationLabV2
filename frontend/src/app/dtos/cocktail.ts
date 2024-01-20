import {IngredientDto} from "./ingredient";
import { List } from 'immutable'; // Import List from Immutable.js

export interface CocktailListDto {
  id: number;
  name: string;
  imagePath: string;
}


/**
 * A Dto for storing the data of a cocktail in Group Detail View which also contains the Rating of the User
 */
export interface CocktailListMenuDto {
  id: number;
  name: string;
  imagePath: string;
  rating: FeedbackState;
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

export enum FeedbackState {
  Like = 'like',
  Dislike = 'dislike',
  NotVoted = 'notVoted',
}

export interface CocktailFeedbackDto {
  cocktailId: number;
  groupId: number;
  rating: string;
}


