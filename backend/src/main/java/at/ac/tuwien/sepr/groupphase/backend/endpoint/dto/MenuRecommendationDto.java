package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.util.List;

/**
 * DTO for a recommended menu.
 */
public class MenuRecommendationDto {

    List<CocktailListDto> cocktailMenu;
    Float lv;

    public MenuRecommendationDto(List<CocktailListDto> cocktailMenus, Float lv) {
        this.cocktailMenu = cocktailMenus;
        this.lv = lv;
    }

    public MenuRecommendationDto() {
    }

    public Float getLv() {
        return lv;
    }

    public void setLv(Float lv) {
        this.lv = lv;
    }

    public List<CocktailListDto> getCocktailMenu() {
        return cocktailMenu;
    }

    public void setCocktailMenu(List<CocktailListDto> cocktailMenu) {
        this.cocktailMenu = cocktailMenu;
    }
}
