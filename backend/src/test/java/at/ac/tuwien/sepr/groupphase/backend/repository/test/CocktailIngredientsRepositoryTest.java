package at.ac.tuwien.sepr.groupphase.backend.repository.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import at.ac.tuwien.sepr.groupphase.backend.entity.Cocktail;
import at.ac.tuwien.sepr.groupphase.backend.entity.CocktailIngredients;
import at.ac.tuwien.sepr.groupphase.backend.entity.CocktailIngredientsKey;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredient;
import at.ac.tuwien.sepr.groupphase.backend.repository.CocktailIngredientsRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.CocktailRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.IngredientsRepository;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("generateData")
@SpringBootTest
public class CocktailIngredientsRepositoryTest {

    @Autowired
    private CocktailIngredientsRepository cocktailIngredientsRepository;

    @Autowired
    private CocktailRepository cocktailRepository;

    @Autowired
    private IngredientsRepository ingredientsRepository;

    @Test
    public void findByIngredientNameAndCocktailName_searchByIngredientAndCocktailName_Expected1() {
        int expected = 0;
        int result = cocktailIngredientsRepository.findByIngredientNameContainingIgnoreCaseAndCocktailNameContainingIgnoreCase("Mojito", "Lime").size();

        assertEquals(expected, result);
    }

    @Test
    public void findByIngredientNameAndCocktailName_searchByCocktailName_Expected1() {
        int expected = 4;
        int result = cocktailIngredientsRepository.findByCocktailNameContainingIgnoreCase("Mojito").size();

        assertEquals(expected, result);
    }

    @Test
    public void findByIngredientNameAndCocktailName_searchByIngredientsName_ExpectedTwentyThree() {
        int expected = 23;
        int result = cocktailIngredientsRepository.findByIngredientNameContainingIgnoreCase("Lime").size();

        assertEquals(expected, result);
    }

}
