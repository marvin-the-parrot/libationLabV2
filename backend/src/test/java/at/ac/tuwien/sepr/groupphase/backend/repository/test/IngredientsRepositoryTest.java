package at.ac.tuwien.sepr.groupphase.backend.repository.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import at.ac.tuwien.sepr.groupphase.backend.repository.IngredientsRepository;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("generateData")
@SpringBootTest
public class IngredientsRepositoryTest {

	@Autowired
	private IngredientsRepository ingredientsRepository;

	@Test
	public void findByNameContainingIgnoreCase_searchingForIngredientCreme_findingFiveResults() {
		int expected = 5;
        int result = ingredientsRepository.findByNameContainingIgnoreCaseOrderByName("creme").size();

        assertEquals(expected, result);
	}

}
