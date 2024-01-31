import {CocktailFeedbackHostDto, CocktailListDto, CocktailListMenuDto} from "./cocktail";

export interface MenuCocktailsDto {
  groupId: number
  cocktailsList: CocktailListDto[];
}


/**
 * A Dto for storing the data of a cocktailCard in Group Detail View which also contains the Rating of the User
 */
export interface MenuCocktailsDetailViewDto {
  groupId: number
  cocktailsList: CocktailListMenuDto[];
}

export interface MenuCocktailsDetailViewHostDto {
  groupId: number;
  cocktailsList: CocktailFeedbackHostDto[];
}


