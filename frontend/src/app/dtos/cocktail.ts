import {IngredientDto, IngredientListDto} from "./ingredient";
import { List } from "lodash";

export interface CocktailListDto {
  id: number;
  name: string;
  imagePath: string;
  ingredientsName: List<string>;
  preferenceName: List<string>;
}

export interface CocktailOverviewDto {
  id: number;
  name: string;
  imagePath: string;
  ingredients: IngredientDto[];
  instructions: string;
}


