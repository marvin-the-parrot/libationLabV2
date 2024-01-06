import {CocktailListDto, CocktailOverviewDto} from "./cocktail";
import {MenuRecommendation} from "./menuRecommendation";

export interface RecommendedMenues {
  id: number;
  menuList: MenuRecommendation[];
}
