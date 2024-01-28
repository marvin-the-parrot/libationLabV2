package at.ac.tuwien.sepr.groupphase.backend.repository.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import at.ac.tuwien.sepr.groupphase.backend.repository.CocktailIngredientsRepository;

import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("generateData")
@SpringBootTest
public class CocktailIngredientsRepositoryTest {

    @Autowired
    private CocktailIngredientsRepository cocktailIngredientsRepository;

    @Test
    public void findByIngredientNameAndCocktailName_searchByIngredientAndCocktailName_Expected1() {
        int expected = 0;
        int result = cocktailIngredientsRepository.findByIngredientNameContainingIgnoreCaseAndCocktailNameContainingIgnoreCase("Mojito", "Lime").size();

        assertEquals(expected, result);
    }

    @Test
    public void findByIngredientNameAndCocktailName_searchByCocktailName_Expected5() {
        int expected = 5;
        int result = cocktailIngredientsRepository.findByCocktailNameContainingIgnoreCase("Mojito").size();

        assertEquals(expected, result);
    }

    @Test
    public void findByIngredientNameAndCocktailName_searchByIngredientsName_ExpectedTwentyFive() {
        int expected = 25;
        int result = cocktailIngredientsRepository.findByIngredientNameContainingIgnoreCase("Lime").size();

        assertEquals(expected, result);
    }

}
