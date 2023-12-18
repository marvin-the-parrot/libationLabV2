package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.util.List;

public class IngredientSuggestionDto {
    private long id;
    private String name;
    private List<CocktailOverviewDto> possibleCocktails;

    public IngredientSuggestionDto(long id, String name, List<CocktailOverviewDto> possibleCocktails) {
        this.id = id;
        this.name = name;
        this.possibleCocktails = possibleCocktails;
    }


}
