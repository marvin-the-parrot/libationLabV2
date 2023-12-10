package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.CocktailIngredients;
import at.ac.tuwien.sepr.groupphase.backend.entity.CocktailIngredientsKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CocktailIngredientsRepository extends JpaRepository<CocktailIngredients, CocktailIngredientsKey> {

    List<CocktailIngredients> findByIngredientNameContainingIgnoreCaseAndCocktailNameContainingIgnoreCase(String ingredientName, String cocktailName);

    List<CocktailIngredients> findByIngredientNameContainingIgnoreCase(String ingredientName);

    List<CocktailIngredients> findByCocktailNameContainingIgnoreCase(String cocktailName);

}
