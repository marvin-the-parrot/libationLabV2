import { List } from "lodash";

export interface CocktailListDto {
  id: number;
  name: string;
  imagePath: string;
  ingredientsName: List<string>
}


