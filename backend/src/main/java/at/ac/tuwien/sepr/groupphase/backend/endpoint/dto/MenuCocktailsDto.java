package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.util.List;

/**
 * DTO for a cocktail menu.
 */
public class MenuCocktailsDto {

    Long groupId;
    List<CocktailOverviewDto> cocktailsList;

    public MenuCocktailsDto() {
    }

    public MenuCocktailsDto(Long groupId, List<CocktailOverviewDto> cocktailsList) {
        super();
        this.groupId = groupId;
        this.cocktailsList = cocktailsList;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public List<CocktailOverviewDto> getCocktailsList() {
        return cocktailsList;
    }

    public void setCocktailsList(List<CocktailOverviewDto> cocktailsList) {
        this.cocktailsList = cocktailsList;
    }
}
