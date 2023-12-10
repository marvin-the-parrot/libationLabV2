package at.ac.tuwien.sepr.groupphase.backend.service.test;

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
import at.ac.tuwien.sepr.groupphase.backend.service.CocktailIngredientService;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("generateData")
@SpringBootTest
public class CocktailServiceImplTest {

    @Autowired
    private CocktailIngredientService cocktailIngredientService;

    @Autowired
    private CocktailIngredientsRepository cocktailIngredientsRepository;

    @Autowired
    private CocktailRepository cocktailRepository;

    @Autowired
    private IngredientsRepository ingredientsRepository;

    @Test
    public void searchCocktailByCocktailNameAndIngredientName_searchByIngredientAndCocktailName_Expected1() {
        int expected = 1;
        int result = cocktailIngredientService.searchCocktailByCocktailNameAndIngredientName("Mojito", "Lime").size();
        //TODO: adjust these tests to new dataset
        assertEquals(expected, result);
    }

    @Test
    public void searchCocktailByCocktailNameAndIngredientName_searchByCocktailName_Expected4() {
        int expected = 4;
        int result = cocktailIngredientService.searchCocktailByCocktailNameAndIngredientName("Mojito", null).size();

        assertEquals(expected, result);
    }

    @Test
    public void searchCocktailByCocktailNameAndIngredientName_searchByIngredientsName_Expected9() {
        int expected = 9;
        int result = cocktailIngredientService.searchCocktailByCocktailNameAndIngredientName(null, "Lime").size();

        assertEquals(expected, result);
    }

}
