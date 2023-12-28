package at.ac.tuwien.sepr.groupphase.backend.service.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import at.ac.tuwien.sepr.groupphase.backend.repository.CocktailIngredientsRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.CocktailRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.IngredientsRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.CocktailService;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("generateData")
@SpringBootTest
public class CocktailServiceImplTest {

    @Autowired
    private CocktailService cocktailIngredientService;

    @Autowired
    private CocktailIngredientsRepository cocktailIngredientsRepository;

    @Autowired
    private CocktailRepository cocktailRepository;

    @Autowired
    private IngredientsRepository ingredientsRepository;

    @Test
    public void searchCocktailByCocktailNameAndIngredientName_searchByIngredientAndCocktailName_Expected1() {
        int expected = 1;
        int result = cocktailIngredientService.searchCocktailByCocktailNameAndIngredientName("Mojito", "Lime", null).size();
        //TODO: adjust these tests to new dataset
        assertEquals(expected, result);
    }

    @Test
    public void searchCocktailByCocktailNameAndIngredientName_searchByCocktailName_Expected1() {
        int expected = 1;
        int result = cocktailIngredientService.searchCocktailByCocktailNameAndIngredientName("Mojito", null, null).size();

        assertEquals(expected, result);
    }

    @Test
    public void searchCocktailByCocktailNameAndIngredientName_searchByIngredientsName_ExpectedTwentyTwo() {
        int expected = 22;
        int result = cocktailIngredientService.searchCocktailByCocktailNameAndIngredientName(null, "Lime", null).size();

        assertEquals(expected, result);
    }

}
