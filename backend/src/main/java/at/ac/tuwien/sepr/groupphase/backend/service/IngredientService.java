package at.ac.tuwien.sepr.groupphase.backend.service;

import java.util.List;

import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredient;

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
    public List<Ingredient> searchIngredients(String ingredientsName);

    /**
     * Get all ingredients.
     *
     * @return all ingredients
     */
    public List<Ingredient> getAllGroupIngredients(Long groupId);

}
