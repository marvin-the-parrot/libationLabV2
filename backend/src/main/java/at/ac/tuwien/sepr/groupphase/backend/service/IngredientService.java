package at.ac.tuwien.sepr.groupphase.backend.service;

import java.util.List;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.IngredientGroupDto;
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

}
