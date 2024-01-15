package at.ac.tuwien.sepr.groupphase.backend.repository;

import java.util.List;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.CocktailIngredients;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;

/**
 * Repository of Ingredients entity.
 */
@Repository
@Transactional
public interface IngredientsRepository extends JpaRepository<Ingredient, Long> {

    List<Ingredient> findByNameContainingIgnoreCaseOrderByName(String name);

    List<Ingredient> findAllByOrderByName();

    List<Ingredient> findAllByApplicationUserInOrderByName(List<ApplicationUser> applicationUser);

    List<Ingredient> findAllByApplicationUser(ApplicationUser applicationUser);

    List<Ingredient> findFirst10ByNameNotInAndNameIgnoreCaseContainingOrderByName(List<String> names, String ingredientName);

    List<Ingredient> findFirst10ByNameIgnoreCaseContainingOrderByName(String ingredientName);

    Ingredient findByName(String name);

    List<Ingredient> findByCocktailIngredientsIn(List<CocktailIngredients> cocktailIngredients);
}
