package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.util.List;

/**
 * DTO to send ingredient Suggestions to the front end.
 */
public class IngredientSuggestionDto {
    private long id;
    private String name;
    private List<CocktailOverviewDto> possibleCocktails;

    public IngredientSuggestionDto(long id, String name, List<CocktailOverviewDto> possibleCocktails) {
        this.id = id;
        this.name = name;
        this.possibleCocktails = possibleCocktails;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<CocktailOverviewDto> getPossibleCocktails() {
        return possibleCocktails;
    }
}
