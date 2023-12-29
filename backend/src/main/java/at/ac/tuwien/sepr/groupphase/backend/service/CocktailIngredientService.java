package at.ac.tuwien.sepr.groupphase.backend.service;

import java.util.List;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CocktailListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CocktailOverviewDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CocktailSerachDto;

/**
 * Service for Cocktail and CocktailIngredient Entity.
 */
public interface CocktailIngredientService {

    /**
     * Searching for cocktail by cocktails name and ingredients name.
     *
     * @param searchParameters the search parameters
     * @return cocktails
     */
    List<CocktailListDto> searchCocktails(CocktailSerachDto searchParameters);
    //List<CocktailListDto> searchCocktailByCocktailNameAndIngredientName(String cocktailsName, String ingredientsName, String preferenceName);

    /**
     * Searching for cocktails that can be mixed with the given ingredients.
     *
     * @return cocktails
     */
    List<CocktailOverviewDto> getMixableCocktails(Long groupId);
}
