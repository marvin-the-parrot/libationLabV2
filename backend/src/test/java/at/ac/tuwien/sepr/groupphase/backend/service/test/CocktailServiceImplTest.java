package at.ac.tuwien.sepr.groupphase.backend.service.test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CocktailListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CocktailSerachDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import at.ac.tuwien.sepr.groupphase.backend.repository.CocktailIngredientsRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.CocktailRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.IngredientsRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.CocktailService;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@SpringBootTest
@ActiveProfiles({"test", "generateData"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CocktailServiceImplTest {

    @Autowired
    private CocktailService cocktailIngredientService;

    @Autowired
    private CocktailIngredientsRepository cocktailIngredientsRepository;

    @Autowired
    private CocktailRepository cocktailRepository;

    @Autowired
    private IngredientsRepository ingredientsRepository;

    /*@Test
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
    }*/

    @Test
    public void searchCocktails_searchCocktailsWithAllValues_ExpectedCorrectResult() {
        CocktailSerachDto cocktailSerachDto = new CocktailSerachDto();
        cocktailSerachDto.setCocktailName("s");
        cocktailSerachDto.setIngredientsName("Vodka,Lime Juice");
        cocktailSerachDto.setPreferenceName("Alcoholic");
        int expected = 3;
        List<CocktailListDto> result = cocktailIngredientService.searchCocktails(cocktailSerachDto);
        assertAll(
            () -> assertEquals(result.size(), expected),
            () -> assertEquals("Moscow Mule", result.get(0).getName()),
            () -> assertEquals("Army special", result.get(1).getName()),
            () -> assertEquals("Cosmopolitan", result.get(2).getName())
        );
    }

    @Test
    public void searchCocktails_searchCocktailsWithoutValues_ExpectedCorrectResult() {
        CocktailSerachDto cocktailSerachDto = new CocktailSerachDto();
        int expected = 108;
        List<CocktailListDto> result = cocktailIngredientService.searchCocktails(cocktailSerachDto);
        assertAll(
            () -> assertEquals(expected, result.size())
        );
    }

    @Test
    public void searchCocktails_searchCocktailsWithoutName_ExpectedCorrectResult() {
        CocktailSerachDto cocktailSerachDto = new CocktailSerachDto();
        cocktailSerachDto.setIngredientsName("Lime Juice,Tequila");
        cocktailSerachDto.setPreferenceName("Alcoholic");
        int expected = 1;
        List<CocktailListDto> result = cocktailIngredientService.searchCocktails(cocktailSerachDto);
        assertAll(
            () -> assertEquals(expected, result.size()),
            () -> assertEquals("Margarita", result.get(0).getName())
        );
    }

    @Test
    public void searchCocktails_searchCocktailsWithIngredientsThatResultIsEmpty_ExpectedCorrectResult() {
        CocktailSerachDto cocktailSerachDto = new CocktailSerachDto();
        cocktailSerachDto.setIngredientsName("Lime Juice,Tequila,Advocaat");
        int expected = 0;
        List<CocktailListDto> result = cocktailIngredientService.searchCocktails(cocktailSerachDto);
        assertAll(
            () -> assertEquals(expected, result.size())
        );
    }
}
