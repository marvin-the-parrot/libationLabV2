package at.ac.tuwien.sepr.groupphase.backend.repository;

import java.util.List;

import at.ac.tuwien.sepr.groupphase.backend.entity.Cocktail;
import org.springframework.data.jpa.repository.JpaRepository;

import at.ac.tuwien.sepr.groupphase.backend.entity.CocktailIngredients;
import at.ac.tuwien.sepr.groupphase.backend.entity.CocktailIngredientsKey;


public interface CocktailIngredientsRepository extends JpaRepository<CocktailIngredients, CocktailIngredientsKey> {

    List<CocktailIngredients> findByIngredientNameContainingIgnoreCaseAndCocktailNameContainingIgnoreCase(String ingredientName, String cocktailName);

    List<CocktailIngredients> findByIngredientNameContainingIgnoreCase(String ingredientName);

    List<CocktailIngredients> findByIngredientNameEqualsIgnoreCase(String ingredientName);

    List<CocktailIngredients> findByCocktailNameContainingIgnoreCase(String cocktailName);

    List<CocktailIngredients> findAllByCocktail(Cocktail cocktail);


}
