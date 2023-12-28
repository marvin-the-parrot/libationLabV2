package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.CocktailIngredients;
import at.ac.tuwien.sepr.groupphase.backend.entity.Preference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Cocktail;

import java.util.List;
import java.util.Set;

@Repository
public interface CocktailRepository extends JpaRepository<Cocktail, Long> {

    List<Cocktail> findAllByOrderByNameAsc();

    List<Cocktail> findByNameContainingIgnoreCase(String name);

    List<Cocktail> findByPreferencesIn(List<Preference> preferences);

    List<Cocktail> findDistinctByCocktailIngredientsIn(List<CocktailIngredients> ingredients);
}
