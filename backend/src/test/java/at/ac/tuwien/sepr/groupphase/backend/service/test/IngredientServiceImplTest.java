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
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("generateData")
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

}
