package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredient;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

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

    List<Ingredient> findByNameIn(List<String> names);
}
