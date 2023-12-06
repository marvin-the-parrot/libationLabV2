package at.ac.tuwien.sepr.groupphase.backend.repository;

import java.util.List;

import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;

/**
 * Repository of Ingredients entity.
 */
@Repository
@Transactional
public interface IngredientsRepository extends JpaRepository<Ingredient, Long> {

    @Query("SELECT i FROM Ingredient i WHERE i.name LIKE %:ingredientsName%")
    List<Ingredient> searchIngredients(@Param("ingredientsName") String ingredientsName);

}
