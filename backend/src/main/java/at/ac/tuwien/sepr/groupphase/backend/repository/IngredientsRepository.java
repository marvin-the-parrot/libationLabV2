package at.ac.tuwien.sepr.groupphase.backend.repository;

import java.util.List;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
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

    List<Ingredient> findByNameContainingIgnoreCase(String name);

    List<Ingredient> findAllByApplicationUserIn(List<ApplicationUser> applicationUser);

    List<Ingredient> findAllByApplicationUser(ApplicationUser applicationUser);

    List<Ingredient> findFirst5ByNameNotInAndNameIgnoreCaseContaining(List<String> names, String ingredientName);

    Ingredient findByName(String name);
}
