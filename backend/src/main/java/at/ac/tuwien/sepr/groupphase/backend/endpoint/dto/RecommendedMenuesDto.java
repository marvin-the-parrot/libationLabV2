package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.util.List;

public class RecommendedMenuesDto {

    Long id;
    List<MenuRecommendationDto> cocktailsList;

    public RecommendedMenuesDto(Long id, List<MenuRecommendationDto> cocktailsList) {
        this.id = id;
        this.cocktailsList = cocktailsList;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<MenuRecommendationDto> getCocktailsList() {
        return cocktailsList;
    }

    public void setCocktailsList(List<MenuRecommendationDto> cocktailsList) {
        this.cocktailsList = cocktailsList;
    }
}
