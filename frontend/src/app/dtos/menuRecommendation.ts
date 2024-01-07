import {CocktailListDto, CocktailOverviewDto} from "./cocktail";

export interface MenuRecommendation {
    cocktailMenu: CocktailListDto[];
    lv: number;
  }
