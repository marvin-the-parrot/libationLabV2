package at.ac.tuwien.sepr.groupphase.backend.service;

import java.util.List;

import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredient;

/**
 * Service for Ingredients Entity.
 */
public interface IngredientsService {

    /**
     * Searching for ingredients.
     *
     * @param ingredientsName name of ingredients
     * @return ingredients with searched name
     */
    public List<Ingredient> searchIngredients(String ingredientsName);

}
