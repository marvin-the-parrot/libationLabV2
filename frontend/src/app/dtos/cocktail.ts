import {IngredientDto} from "./ingredient";

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
  rating: FeedbackState;
}

/**
 * A Dto for storing the data of a cocktail in Group Detail View (when you are Host) which also contains amount of Ratings for the cocktail
 */
export interface CocktailFeedbackHostDto {
  id: number;
  name: string;
  positiveRating: number;
  negativeRating: number;
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

export interface CocktailTagSearchDto {
  cocktailName?: string;
  selectedIngredients?: string[];
  selectedPreferences?: string[];
}

export enum FeedbackState {
  Like,
  Dislike,
  NotVoted,
}

export interface CocktailFeedbackDto {
  cocktailId: number;
  groupId: number;
  rating: FeedbackState;
}


