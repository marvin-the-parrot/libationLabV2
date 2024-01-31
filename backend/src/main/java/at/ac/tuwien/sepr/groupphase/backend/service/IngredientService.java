package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.IngredientGroupDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.IngredientListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.IngredientSuggestionDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import java.util.List;

/**
 * Service for Ingredients Entity.
 */
public interface IngredientService {

    /**
     * Searching for ingredients.
     *
     * @param ingredientsName name of ingredients
     * @return ingredients with searched name
     * @throws JsonProcessingException in case of api exception
     * @throws JsonMappingException    in case of mapping exception
     */
    List<IngredientListDto> searchIngredients(String ingredientsName) throws JsonMappingException, JsonProcessingException;

    /**
     * Get all ingredients.
     *
     * @return all ingredients
     */
    List<IngredientGroupDto> getAllGroupIngredients(Long groupId) throws NotFoundException;

    /**
     * Retrieve all stored ingredients, that match the given parameters.
     * The parameters may include a limit on the amount of results to return.
     *
     * @param searchParams parameters to search ingredients by
     * @return a stream of ingredients matching the parameters
     */
    List<IngredientListDto> searchUserIngredients(String searchParams);

    /**
     * Retrieve all stored ingredients, are associated with a user.
     *
     * @return a stream of ingredients belonging to a user
     */
    List<IngredientListDto> getUserIngredients();

    /**
     * Add ingredients to user.
     *
     * @param ingredientListDto ingredients to add
     */
    List<IngredientListDto> addIngredientsToUser(IngredientListDto[] ingredientListDto) throws ConflictException;

    /**
     * Get ingredient suggestions for a group.
     *
     * @param groupId id of the group
     * @return list of ingredient suggestions
     * @throws NotFoundException if the group does not exist
     * @throws ConflictException if the current user is not a member of the group or not the host
     */
    List<IngredientSuggestionDto> getIngredientSuggestions(Long groupId) throws NotFoundException, ConflictException;
}
