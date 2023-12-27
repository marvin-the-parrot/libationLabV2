package at.ac.tuwien.sepr.groupphase.backend.repository;

import java.util.List;

import at.ac.tuwien.sepr.groupphase.backend.entity.Preference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository of CocktailPreference entity.
 */
/*@Repository
public interface CocktailPreferenceRepository extends JpaRepository<CocktailPreference, CocktailPreferenceKey> {

    List<Preference> findByPreferenceNameContainingIgnoreCaseAndCocktailNameContainingIgnoreCase(String preferenceName, String cocktailName);

    List<CocktailPreference> findByPreferenceNameContainingIgnoreCase(String preferenceName);

    List<CocktailPreference> findByCocktailNameContainingIgnoreCase(String cocktailName);

}
*/