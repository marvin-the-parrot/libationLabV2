package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.CocktailIngredients;
import at.ac.tuwien.sepr.groupphase.backend.entity.CocktailIngredientsKey;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Repository of CocktailIngredients entity.
 */
public interface CocktailIngredientsRepository extends JpaRepository<CocktailIngredients, CocktailIngredientsKey> {

    /**
     * Find all cocktail ingredients by ingredient name and cocktail name.
     *
     * @param ingredientName ingredient name
     * @param cocktailName cocktail name
     * @return List of cocktail ingredients
     */
    List<CocktailIngredients> findByIngredientNameContainingIgnoreCaseAndCocktailNameContainingIgnoreCase(String ingredientName, String cocktailName);

    /**
     * Find all cocktail ingredients by ingredient name.
     *
     * @param ingredientName ingredient name
     * @return List of cocktail ingredients
     */
    List<CocktailIngredients> findByIngredientNameContainingIgnoreCase(String ingredientName);

    /**
     * Find all cocktail ingredients by cocktail name.
     *
     * @param cocktailName cocktail name
     * @return List of cocktail ingredients
     */
    List<CocktailIngredients> findByCocktailNameContainingIgnoreCase(String cocktailName);

    /**
     * Counts how many ingredients a cocktail has.
     *
     * @return List of how many ingredients each cocktail has
     */
    @Query("SELECT ci.cocktailIngredientsKey.cocktail, count(ci.cocktailIngredientsKey.ingredient) FROM CocktailIngredients ci GROUP by ci.cocktailIngredientsKey.cocktail")
    List<Object[]> countIngredientsByCocktail();

    /**
     * Counts how many ingredients of each cocktail are in this ingredient list have.
     *
     * @param ingredients List of ingredients
     * @return List of how many ingredients of each cocktail are in this ingredient list have
     */
    @Query("SELECT ci.cocktailIngredientsKey.cocktail, count(ci.cocktailIngredientsKey.ingredient) FROM CocktailIngredients ci WHERE ci.ingredient IN ?1 GROUP by ci.cocktailIngredientsKey.cocktail")
    List<Object[]> countIngredientsByCocktailsIn(List<Ingredient> ingredients);


}
