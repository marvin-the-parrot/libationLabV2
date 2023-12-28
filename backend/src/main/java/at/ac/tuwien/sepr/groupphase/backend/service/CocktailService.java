package at.ac.tuwien.sepr.groupphase.backend.service;

import java.util.List;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CocktailListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CocktailOverviewDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.IngredientListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PreferenceListDto;

/**
 * Service for Cocktail and CocktailIngredient Entity.
 */
public interface CocktailService {

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

    /**
     * Retrieve all stored ingredients, that match the given parameters.
     * The parameters may include a limit on the amount of results to return.
     *
     * @param searchParams parameters to search ingredients by
     * @return a stream of ingredients matching the parameters
     */
    List<IngredientListDto> searchAutoIngredients(String searchParams);

    /**
     * Retrieve all stored ingredients, that match the given parameters.
     * The parameters may include a limit on the amount of results to return.
     *
     * @param searchParams parameters to search ingredients by
     * @return a stream of ingredients matching the parameters
     */
    List<PreferenceListDto> searchAutoPreferences(String searchParams);

}
