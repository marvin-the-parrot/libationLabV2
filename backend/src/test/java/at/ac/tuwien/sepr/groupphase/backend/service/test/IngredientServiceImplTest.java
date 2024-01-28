package at.ac.tuwien.sepr.groupphase.backend.service.test;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.IngredientListDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.IngredientsRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.IngredientServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles({"test", "generateData"})
@SpringBootTest
public class IngredientServiceImplTest {

    @Autowired
    private IngredientServiceImpl ingredientServiceImpl;

    @Test
    public void findByNameContainingIgnoreCase_searchingForIngredientVery_findingTwoResult() throws JsonProcessingException {
        int expected = 4;
        int result = ingredientServiceImpl.searchIngredients("Lemon").size();

        assertEquals(expected, result);
    }

    @Test
    public void findByNameContainingIgnoreCase_searchingForIngredientBabyOilWhichIsNotInDb() throws JsonProcessingException {
        List<IngredientListDto> result = ingredientServiceImpl.searchIngredients("Baby Oil");

        assertEquals(null, result);
    }

    @Test
    @WithMockUser(username = "user1@email.com")
    public void getIngredientSuggestions_fromUserOutsideGroup_expectedException() {
        ConflictException exception = assertThrows(ConflictException.class, () -> ingredientServiceImpl.getIngredientSuggestions(3L));
        assertEquals("Getting ingredient suggestions failed.", exception.summary());
        assertEquals("User is not a member of the group", exception.errors().get(0));
    }

    @Test
    @WithMockUser(username = "user1@email.com")
    public void getIngredientSuggestions_fromUserThatIsNotHost_expectedException() {
        ConflictException exception = assertThrows(ConflictException.class, () -> ingredientServiceImpl.getIngredientSuggestions(2L));
        assertEquals("Getting ingredient suggestions failed.", exception.summary());
        assertEquals("User is not the host of the group", exception.errors().get(0));
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
        assertEquals(69, ingredients.size());

    }

    @Test
    public void getAllGroupIngredients_withInvalidGroupId_expectedException() {
        var exception = assertThrows(NotFoundException.class, () -> ingredientServiceImpl.getAllGroupIngredients(100L));
        assertEquals("Group not found", exception.getMessage());
    }

}
