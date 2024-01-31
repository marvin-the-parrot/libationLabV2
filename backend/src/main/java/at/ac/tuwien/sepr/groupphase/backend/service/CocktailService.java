package at.ac.tuwien.sepr.groupphase.backend.service;

import java.util.List;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CocktailDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CocktailListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CocktailSerachDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.IngredientListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PreferenceListDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;

/**
 * Service for Cocktail and CocktailIngredient Entity.
 */
public interface CocktailService {

    /**
     * Searching for cocktail by cocktails name and ingredients name and preferences (tag based search).
     *
     * @param searchParameters the search parameters on which to search
     * @return a list of cocktails matching the search parameters, may be empty if no cocktail matches
     */
    List<CocktailListDto> searchCocktails(CocktailSerachDto searchParameters);

    /**
     * Searching for cocktails that can be mixed with the given ingredients.
     *
     * @return cocktails
     */
    List<CocktailDetailDto> getMixableCocktails(Long groupId);

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

    /**
     * Retrieve a cocktail by its id from the database.
     *
     * @param id the id of the cocktail to retrieve
     * @return the cocktail with the given id
     */
    CocktailDetailDto getCocktailById(Long id) throws NotFoundException;
}
