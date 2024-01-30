import {CocktailListDto} from "./cocktail";

export interface MenuRecommendation {
    cocktailMenu: CocktailListDto[];
    lv: number;
  }
