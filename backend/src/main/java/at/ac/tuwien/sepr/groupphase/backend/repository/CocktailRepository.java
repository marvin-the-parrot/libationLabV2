package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Cocktail;
import at.ac.tuwien.sepr.groupphase.backend.entity.Feedback;
import at.ac.tuwien.sepr.groupphase.backend.entity.Preference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * Repository of Cocktail entity.
 */
@Repository
public interface CocktailRepository extends JpaRepository<Cocktail, Long> {

    /**
     * Find all cocktails ordered by name.
     *
     * @return List of cocktails
     */
    List<Cocktail> findAllByOrderByNameAsc();

    /**
     * Find all cocktails by name.
     *
     * @param name name
     * @return List of cocktails
     */
    List<Cocktail> findByNameContainingIgnoreCase(String name);

    /**
     * Find all cocktails by name, id ordered by name.
     *
     * @param preferences List of preferences
     * @param ids List of ids
     * @return List of cocktails
     */
    List<Cocktail> findAllByPreferencesInAndIdIn(List<Preference> preferences, List<Long> ids);

    /**
     * Find all cocktails by id.
     *
     * @param ids List of ids
     * @return Set of cocktails
     */
    Set<Cocktail> findByIdIn(List<Long> ids);

    /**
     * Find all cocktails by feedbacks.
     *
     * @param feedbacks List of feedbacks
     * @return List of cocktails
     */
    List<Cocktail> findDistinctByFeedbacksIn(List<Feedback> feedbacks);

    /**
     * Find all cocktails containing the ingredients in the list.
     *
     * @param ingredientNames List of ingredient names
     * @param totalIngredients Number of ingredients
     * @return List of cocktails
     */
    @Query("SELECT c FROM Cocktail c "
        + "JOIN c.cocktailIngredients ci "
        + "WHERE ci.ingredient.name IN :ingredientNames "
        + "GROUP BY c "
        + "HAVING COUNT(DISTINCT ci.ingredient) = :totalIngredients")
    List<Cocktail> findCocktailsWithIngredients(@Param("ingredientNames") List<String> ingredientNames,
                                                @Param("totalIngredients") int totalIngredients);

    /**
     * Find all cocktails containing the preferences in the list.
     *
     * @param preferenceNames List of preference names
     * @param totalPreferences Number of preferences
     * @return List of cocktails
     */
    @Query("SELECT c FROM Cocktail c "
        + "JOIN c.preferences p "
        + "WHERE p.name IN :preferenceNames "
        + "GROUP BY c "
        + "HAVING COUNT(DISTINCT p) = :totalPreferences")
    List<Cocktail> findCocktailsWithPreferences(@Param("preferenceNames") List<String> preferenceNames,
                                                @Param("totalPreferences") int totalPreferences);
}
