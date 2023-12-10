package at.ac.tuwien.sepr.groupphase.backend.repository.test;

import at.ac.tuwien.sepr.groupphase.backend.entity.Cocktail;
import at.ac.tuwien.sepr.groupphase.backend.entity.CocktailIngredients;
import at.ac.tuwien.sepr.groupphase.backend.entity.CocktailIngredientsKey;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredient;
import at.ac.tuwien.sepr.groupphase.backend.repository.CocktailIngredientsRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.CocktailRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.IngredientsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class CocktailIngredientsRepositoryTest {

    @Autowired
    private CocktailIngredientsRepository cocktailIngredientsRepository;

    @Autowired
    private CocktailRepository cocktailRepository;

    @Autowired
    private IngredientsRepository ingredientsRepository;

    @BeforeEach
    public void setUp() {
        cocktailIngredientsRepository.deleteAll();
        ingredientsRepository.deleteAll();
        cocktailRepository.deleteAll();
        Ingredient ingredient1 = new Ingredient();
        ingredient1.setId(9999L);
        ingredient1.setName("Tonic Lemon Water");

        Ingredient ingredient2 = new Ingredient();
        ingredient2.setId(9998L);
        ingredient2.setName("Orange juice");

        ingredientsRepository.save(ingredient1);
        ingredientsRepository.save(ingredient2);

        Cocktail cocktail1 = new Cocktail();
        cocktail1.setId(9999L);
        cocktail1.setImagePath("https://www.thecocktaildb.com/images/ingredients/gin-Medium.png");
        cocktail1.setName("Unique one");

        Cocktail cocktail2 = new Cocktail();
        cocktail2.setId(9998L);
        cocktail2.setName("Two X");
        cocktail2.setImagePath("https://www.thecocktaildb.com/images/ingredients/vodka-Medium.png");

        cocktailRepository.save(cocktail1);
        cocktailRepository.save(cocktail2);

        CocktailIngredientsKey cocktailIngredientsKey1 = new CocktailIngredientsKey(cocktail1.getId(), ingredient1.getId());
        CocktailIngredientsKey cocktailIngredientsKey2 = new CocktailIngredientsKey(cocktail1.getId(), ingredient2.getId());
        CocktailIngredientsKey cocktailIngredientsKey3 = new CocktailIngredientsKey(cocktail2.getId(), ingredient1.getId());

        CocktailIngredients cocktailIngredients1 = new CocktailIngredients();
        cocktailIngredients1.setCocktailIngredientsKey(cocktailIngredientsKey1);
        cocktailIngredients1.setCocktail(cocktail1);
        cocktailIngredients1.setIngredient(ingredient1);

        CocktailIngredients cocktailIngredients2 = new CocktailIngredients();
        cocktailIngredients2.setCocktailIngredientsKey(cocktailIngredientsKey2);
        cocktailIngredients2.setCocktail(cocktail1);
        cocktailIngredients2.setIngredient(ingredient2);

        CocktailIngredients cocktailIngredients3 = new CocktailIngredients();
        cocktailIngredients3.setCocktailIngredientsKey(cocktailIngredientsKey3);
        cocktailIngredients3.setCocktail(cocktail2);
        cocktailIngredients3.setIngredient(ingredient1);

        cocktailIngredientsRepository.save(cocktailIngredients1);
        cocktailIngredientsRepository.save(cocktailIngredients2);
        cocktailIngredientsRepository.save(cocktailIngredients3);
    }

    @Test
    public void findByIngredientNameAndCocktailName_searchByIngredientAndCocktailName_Expected1() {
        int expected = 1;
        int result = cocktailIngredientsRepository.findByIngredientNameContainingIgnoreCaseAndCocktailNameContainingIgnoreCase("Tonic Lemon Water", "Unique one").size();

        assertEquals(expected, result);
    }

    @Test
    public void findByIngredientNameAndCocktailName_searchByCocktailName_Expected1() {
        int expected = 1;
        int result = cocktailIngredientsRepository.findByCocktailNameContainingIgnoreCase("Two X").size();

        assertEquals(expected, result);
    }

    @Test
    public void findByIngredientNameAndCocktailName_searchByIngredientsName_Expected2() {
        int expected = 2;
        int result = cocktailIngredientsRepository.findByIngredientNameContainingIgnoreCase("Tonic Lemon Water").size();

        assertEquals(expected, result);
    }

}
