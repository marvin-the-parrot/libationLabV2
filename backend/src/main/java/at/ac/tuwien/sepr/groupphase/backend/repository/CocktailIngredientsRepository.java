package at.ac.tuwien.sepr.groupphase.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import at.ac.tuwien.sepr.groupphase.backend.entity.CocktailIngredients;
import at.ac.tuwien.sepr.groupphase.backend.entity.CocktailIngredientsKey;

public interface CocktailIngredientsRepository extends JpaRepository<CocktailIngredients, CocktailIngredientsKey> {

    List<CocktailIngredients> findByIngredientNameAndCocktailName(String ingredientName, String cocktailName);

    List<CocktailIngredients> findByIngredientName(String ingredientName);

    List<CocktailIngredients> findByCocktailName(String cocktailName);

}
