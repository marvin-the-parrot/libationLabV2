import {IngredientDto, IngredientListDto} from "./ingredient";

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


