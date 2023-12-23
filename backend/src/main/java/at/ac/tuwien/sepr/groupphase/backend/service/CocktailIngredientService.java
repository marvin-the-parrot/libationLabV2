package at.ac.tuwien.sepr.groupphase.backend.service;

import java.util.List;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CocktailListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CocktailOverviewDto;

/**
 * Service for Cocktail and CocktailIngredient Entity.
 */
public interface CocktailIngredientService {

    /**
     * Searching for cocktail by cocktails name and ingredients name.
     *
     * @param cocktailsName   name of cocktail
     * @param ingredientsName name of ingredients
     * @param preferenceName  name of preference
     * @return cocktails
     */
    List<CocktailListDto> searchCocktailByCocktailNameAndIngredientName(String cocktailsName, String ingredientsName, String preferenceName);

    /**
     * Searching for cocktails that can be mixed with the given ingredients.
     *
     * @return cocktails
     */
    List<CocktailOverviewDto> getMixableCocktails(Long groupId);
}
