package at.ac.tuwien.sepr.groupphase.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.CocktailIngredients;
import at.ac.tuwien.sepr.groupphase.backend.entity.CocktailPreference;
import at.ac.tuwien.sepr.groupphase.backend.entity.CocktailPreferenceKey;

/**
 * Repository of CocktailPreference entity.
 */
@Repository
public interface CocktailPreferenceRepository extends JpaRepository<CocktailPreference, CocktailPreferenceKey> {

    List<CocktailPreference> findByPreferenceNameContainingIgnoreCaseAndCocktailNameContainingIgnoreCase(String preferenceName, String cocktailName);

    List<CocktailPreference> findByPreferenceNameContainingIgnoreCase(String preferenceName);

    List<CocktailPreference> findByCocktailNameContainingIgnoreCase(String cocktailName);

}
