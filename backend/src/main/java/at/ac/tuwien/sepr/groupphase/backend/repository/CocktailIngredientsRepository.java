package at.ac.tuwien.sepr.groupphase.backend.repository;

import java.util.List;
import java.util.Map;
import java.util.Set;

import at.ac.tuwien.sepr.groupphase.backend.entity.Cocktail;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;

import at.ac.tuwien.sepr.groupphase.backend.entity.CocktailIngredients;
import at.ac.tuwien.sepr.groupphase.backend.entity.CocktailIngredientsKey;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface CocktailIngredientsRepository extends JpaRepository<CocktailIngredients, CocktailIngredientsKey> {

    List<CocktailIngredients> findByIngredientNameContainingIgnoreCaseAndCocktailNameContainingIgnoreCase(String ingredientName, String cocktailName);

    List<CocktailIngredients> findByIngredientNameContainingIgnoreCase(String ingredientName);

    List<CocktailIngredients> findByIngredientNameEqualsIgnoreCase(String ingredientName);

    List<CocktailIngredients> findByCocktailNameContainingIgnoreCase(String cocktailName);

    List<CocktailIngredients> findAllByCocktail(Cocktail cocktail);

    List<CocktailIngredients> findByIngredientNameIgnoreCase(String ingredientName);

    @Query("SELECT ci.cocktailIngredientsKey.cocktail, count(ci.cocktailIngredientsKey.ingredient) FROM CocktailIngredients ci GROUP by ci.cocktailIngredientsKey.cocktail")
    List<Object[]> countIngredientsByCocktail();

    @Query("SELECT ci.cocktailIngredientsKey.cocktail, count(ci.cocktailIngredientsKey.ingredient) FROM CocktailIngredients ci WHERE ci.ingredient IN ?1 GROUP by ci.cocktailIngredientsKey.cocktail")
    List<Object[]> countIngredientsByCocktailsIn(List<Ingredient> ingredients);


}
