package at.ac.tuwien.sepr.groupphase.backend.endpoint.test;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserListDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredient;
import at.ac.tuwien.sepr.groupphase.backend.repository.IngredientsRepository;


@SpringBootTest
@EnableWebMvc
@WebAppConfiguration
public class IngredientEndpointTest {

    @Autowired
    private WebApplicationContext webAppContext;
    
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
	@Autowired
	private IngredientsRepository ingredientsRepository;
    
    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
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
    @WithMockUser(roles = {"USER"})
    public void findByNameContainingIgnoreCase_searchingForIngredientVery_findingTwoResult() throws Exception {

        int expected = 2;
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/ingredients/searchIngredients/{ingredientsName}", "Very")).andExpect(status().isOk()).andReturn();
        String contentResult = mvcResult.getResponse().getContentAsString();
        int result = objectMapper.readValue(contentResult, new TypeReference<List<UserListDto>>() {
        }).size();

        assertEquals(result, expected);
    }

}
