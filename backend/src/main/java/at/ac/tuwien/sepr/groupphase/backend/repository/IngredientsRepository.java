package at.ac.tuwien.sepr.groupphase.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredients;
import jakarta.transaction.Transactional;

/**
 * Repository of Ingredients entity.
 */
@Repository
@Transactional
public interface IngredientsRepository extends JpaRepository<Ingredients, Long> {

    @Query("SELECT i FROM Ingredients i WHERE i.name LIKE %:ingredientsName%")
    List<Ingredients> searchIngredients(@Param("ingredientsName") String ingredientsName);

}
