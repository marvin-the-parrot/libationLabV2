package at.ac.tuwien.sepr.groupphase.backend.service;

import java.util.List;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.IngredientGroupDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.IngredientListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.IngredientSearchExistingUserDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserSearchExistingGroupDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredient;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;

/**
 * Service for Ingredients Entity.
 */
public interface IngredientService {

    /**
     * Searching for ingredients.
     *
     * @param ingredientsName name of ingredients
     * @return ingredients with searched name
     */
    List<Ingredient> searchIngredients(String ingredientsName);

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
    List<IngredientListDto> searchUserIngredients(IngredientSearchExistingUserDto searchParams);

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
    List<IngredientListDto> addIngredientsToUser(IngredientListDto[] ingredientListDto);

}
