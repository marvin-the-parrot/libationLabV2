package at.ac.tuwien.sepr.groupphase.backend.service.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.core.JsonProcessingException;

import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredient;
import at.ac.tuwien.sepr.groupphase.backend.repository.IngredientsRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.IngredientServiceImpl;

@SpringBootTest
public class IngredientServiceImplTest {

    @Autowired
    private IngredientsRepository ingredientsRepository;

    @Autowired
    private IngredientServiceImpl ingredientServiceImpl;

    @BeforeEach
    public void setUp() {
        ingredientsRepository.deleteAll();
        Ingredient firstIngredient = new Ingredient();
        firstIngredient.setId(-999L);
        firstIngredient.setName("VeryUniqueIngredient1");
        ingredientsRepository.save(firstIngredient);
        Ingredient secondIngredient = new Ingredient();
        secondIngredient.setId(-998L);
        secondIngredient.setName("VeryUniqueIngredient2");
        ingredientsRepository.save(firstIngredient);
        Ingredient threeIngredient = new Ingredient();
        threeIngredient.setId(-997L);
        threeIngredient.setName("XxXIngredient");
        ingredientsRepository.save(firstIngredient);
        ingredientsRepository.save(secondIngredient);
        ingredientsRepository.save(threeIngredient);
    }

    @Test
    public void findByNameContainingIgnoreCase_searchingForIngredientVery_findingTwoResult() throws JsonProcessingException {
        int expected = 2;
        int result = ingredientServiceImpl.searchIngredients("Very").size();

        assertEquals(expected, result);
    }

    @Test
    public void findByNameContainingIgnoreCase_searchingForIngredientYeastWhichIsNotInDb_findingOneResult() throws JsonProcessingException {
        int expected = 1;
        int result = ingredientServiceImpl.searchIngredients("Yeast").size();

        assertEquals(expected, result);
    }

}
