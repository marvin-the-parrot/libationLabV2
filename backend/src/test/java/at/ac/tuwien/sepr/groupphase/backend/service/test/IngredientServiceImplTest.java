package at.ac.tuwien.sepr.groupphase.backend.service.test;

import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.IngredientsRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.IngredientServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles({"test", "generateData"})
@SpringBootTest
public class IngredientServiceImplTest {

    @Autowired
    private IngredientsRepository ingredientsRepository;

    @Autowired
    private IngredientServiceImpl ingredientServiceImpl;

    @Test
    public void findByNameContainingIgnoreCase_searchingForIngredientVery_findingTwoResult() throws JsonProcessingException {
        int expected = 7;
        int result = ingredientServiceImpl.searchIngredients("Lemon").size();

        assertEquals(expected, result);
    }

    @Test
    public void findByNameContainingIgnoreCase_searchingForIngredientYeastWhichIsNotInDb_findingOneResult() throws JsonProcessingException {
        int expected = 1;
        int result = ingredientServiceImpl.searchIngredients("Milk").size();

        assertEquals(expected, result);
    }

    @Test
    public void searchIngredients_ingredientNotInOurDbReturnsEntryFromApi_expectedSuccess() {
        var ingredients = assertDoesNotThrow(() -> ingredientServiceImpl.searchIngredients("Onion"));
        assertEquals(1, ingredients.size());
        assertEquals("Onion", ingredients.get(0).getName());
    }

    @Test
    public void searchIngredients_ingredientNotInOurDbAndNoApiEntry_expectedNull() {
        var ingredients = assertDoesNotThrow(() -> ingredientServiceImpl.searchIngredients("Baumkuchen"));
        assertNull(ingredients);
    }

    @Test
    public void getAllGroupIngredients_withValidGroupId_expectedSuccess() {
        var ingredients = assertDoesNotThrow(() -> ingredientServiceImpl.getAllGroupIngredients(1L));
        assertEquals(86, ingredients.size());

    }

    @Test
    public void getAllGroupIngredients_withInvalidGroupId_expectedException() {
        var exception = assertThrows(NotFoundException.class, () -> ingredientServiceImpl.getAllGroupIngredients(100L));
        assertEquals("Group not found", exception.getMessage());
    }

}
