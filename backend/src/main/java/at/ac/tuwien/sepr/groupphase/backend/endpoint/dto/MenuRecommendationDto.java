package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.util.List;

public class MenuRecommendationDto {

    List<CocktailOverviewDto> cocktailMenus;
    Float lv;

    public MenuRecommendationDto( List<CocktailOverviewDto> cocktailMenus, Float lv) {
        this.cocktailMenus = cocktailMenus;
        this.lv = lv;
    }

    public Float getLv() {
        return lv;
    }

    public void setLv(Float lv) {
        this.lv = lv;
    }

    public List<CocktailOverviewDto> getCocktailMenus() {
        return cocktailMenus;
    }

    public void setCocktailMenus(List<CocktailOverviewDto> cocktailMenus) {
        this.cocktailMenus = cocktailMenus;
    }
}
