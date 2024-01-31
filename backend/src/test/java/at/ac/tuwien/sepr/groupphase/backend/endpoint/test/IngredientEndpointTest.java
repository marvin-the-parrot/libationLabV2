package at.ac.tuwien.sepr.groupphase.backend.endpoint.test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.IngredientListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PreferenceListDto;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
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
import at.ac.tuwien.sepr.groupphase.backend.repository.IngredientsRepository;

@ActiveProfiles({"test", "generateData"})
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
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void findByNameContainingIgnoreCase_searchingForIngredientRum_findingEightResult() throws Exception {

        int expected = 8;
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/ingredients/searchIngredients/{ingredientsName}", "Rum")).andExpect(status().isOk()).andReturn();
        String contentResult = mvcResult.getResponse().getContentAsString();
        int result = objectMapper.readValue(contentResult, new TypeReference<List<UserListDto>>() {
        }).size();

        assertEquals(expected, result);
    }

    @Test
    @WithMockUser(username = "user1@email.com")
    public void getIngredientSuggestions_getSuggestionsForGroup1_expectedSuccess() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/ingredients/suggestions/{groupId}", 1)).andExpect(status().isOk()).andReturn();
        var contentResult = mvcResult.getResponse().getContentAsString();
        assertTrue(contentResult.contains("Vodka"));
        assertTrue(contentResult.contains("Maraschino Liqueur"));
        assertTrue(contentResult.contains("Lemon"));
    }

    @Test
    @WithMockUser(username = "user1@email.com")
    public void getIngredientSuggestions_getSuggestionsForGroup999_expectedNotFound() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/ingredients/suggestions/{groupId}", 999)).andExpect(status().isNotFound()).andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString().contains("Group not found"));
    }

    @Test
    @WithMockUser(roles = {"USER"}, username = "user1@email.com")
    public void searchAutocomplete_searchingForIngredientsPositive_FindingResultsContainingR() throws Exception{
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/ingredients/user-ingredients-auto/{ingredientsName}", "r"))
            .andExpect(status().isOk()).andReturn();
        String contentResult = mvcResult.getResponse().getContentAsString();
        List<IngredientListDto> result = objectMapper.readValue(contentResult, new TypeReference<>() {
        });

        assertAll(
            () -> assertTrue(result.size() <= 10),
            () -> assertFalse(result.isEmpty()),
            () -> assertTrue(result.stream().allMatch(item -> item.getName().contains("r")),
                "All names should contain 'r'")
        );
    }

    @Test
    @WithMockUser(roles = {"USER"}, username = "user1@wrongDomain.com")
    public void searchAutocomplete_searchingForIngredientsNegative_userNotFound() throws Exception{
        var response = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/ingredients/user-ingredients-auto/{ingredientsName}", "r"))
            .andExpect(status().isNotFound()).andReturn().getResponse();
        int status = response.getStatus();
        assertAll(
            () -> assertEquals(HttpStatus.NOT_FOUND.value(), status),
            () -> assertTrue(response.getContentAsString().contains("User not found"))
        );
    }

    @Test
    @WithMockUser(roles = {"USER"}, username = "user1@email.com")
    public void getUserIngredients_gettingUserIngredientsPositive_expectedSize() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/ingredients/user-ingredients"))
            .andExpect(status().isOk()).andReturn();
        String contentResult = mvcResult.getResponse().getContentAsString();
        List<IngredientListDto> result = objectMapper.readValue(contentResult, new TypeReference<>() {
        });

        assertAll(
            () -> assertEquals(24, result.size()),
            () -> assertEquals("Apple Brandy", result.get(0).getName()),
            () -> assertEquals("Beer", result.get(1).getName()),
            () -> assertEquals("Bitters", result.get(2).getName())
        );
    }

    @Test
    @WithMockUser(roles = {"USER"}, username = "user1@wrongDomain.com")
    public void getUserIngredients_gettingUserIngredientsNegative_NotFoundException() throws Exception {
        var response = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/ingredients/user-ingredients"))
            .andExpect(status().isNotFound()).andReturn().getResponse();
        int status = response.getStatus();
        assertEquals(HttpStatus.NOT_FOUND.value(), status);

    }

    @Test
    @WithMockUser(roles = {"USER"}, username = "user1@email.com")
    @Transactional
    @Rollback
    public void addUserIngredients_addingIngredientsPositive_expectedSize() throws Exception {
        IngredientListDto[] ingredients = new IngredientListDto[2];
        IngredientListDto darkRum = new IngredientListDto();
        darkRum.setId(56L);
        darkRum.setName("Dark Rum");
        ingredients[0] = darkRum;
        IngredientListDto spicedRum = new IngredientListDto();
        spicedRum.setId(35L);
        spicedRum.setName("Spiced Rum");
        ingredients[1] = spicedRum;

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/ingredients/user-ingredients")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(ingredients))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk()).andReturn();
        String contentResult = mvcResult.getResponse().getContentAsString();
        List<IngredientListDto> result = objectMapper.readValue(contentResult, new TypeReference<>() {
        });

        assertAll(
            () -> assertEquals(2, result.size()),
            () -> assertEquals("Spiced Rum", result.get(0).getName()),
            () -> assertEquals("Dark Rum", result.get(1).getName())
        );
    }

    @Test
    @WithMockUser(roles = {"USER"}, username = "user1@wrongDomain.com")
    public void addUserIngredients_addingIngredientsNegative_NotFoundExceptionBecauseUserNotExists() throws Exception {
        IngredientListDto[] ingredients = new IngredientListDto[2];
        IngredientListDto darkRum = new IngredientListDto();
        darkRum.setId(1L);
        darkRum.setName("Dark Rum");
        ingredients[0] = darkRum;
        IngredientListDto spicedRum = new IngredientListDto();
        spicedRum.setId(2L);
        spicedRum.setName("Spiced rum");
        ingredients[1] = spicedRum;

        var response = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/ingredients/user-ingredients")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(ingredients))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound()).andReturn().getResponse();
        int status = response.getStatus();
        assertAll(
            () -> assertEquals(HttpStatus.NOT_FOUND.value(), status),
            () -> assertTrue(response.getContentAsString().contains("User not found"))
        );
    }

    @Test
    @WithMockUser(roles = {"USER"}, username = "user1@email.com")
    public void addUserIngredients_addingIngredientsNegative_NotFoundExceptionBecauseIngredientDoesNotExist() throws Exception {
        IngredientListDto[] ingredients = new IngredientListDto[2];
        IngredientListDto darkRum = new IngredientListDto();
        darkRum.setId(-1L);
        darkRum.setName("Blue Rum");
        ingredients[0] = darkRum;
        IngredientListDto spicedRum = new IngredientListDto();
        spicedRum.setId(2L);
        spicedRum.setName("Spiced rum");
        ingredients[1] = spicedRum;

        var response = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/ingredients/user-ingredients")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(ingredients))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound()).andReturn().getResponse();
        int status = response.getStatus();
        assertEquals(HttpStatus.NOT_FOUND.value(), status);

    }

    @Test
    @WithMockUser(roles = {"USER"}, username = "user1@email.com")
    public void addUserIngredients_addingIngredientsNegative_ConflictExceptionIngredientDoesHaveWrongID() throws Exception {
        IngredientListDto[] ingredients = new IngredientListDto[2];
        IngredientListDto darkRum = new IngredientListDto();
        darkRum.setId(1L);
        darkRum.setName("Apricot brandy");
        ingredients[0] = darkRum;
        IngredientListDto spicedRum = new IngredientListDto();
        spicedRum.setId(2L);
        spicedRum.setName("Spiced rum");
        ingredients[1] = spicedRum;

        var response = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/ingredients/user-ingredients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ingredients))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isConflict()).andReturn().getResponse();
        int status = response.getStatus();
        assertEquals(HttpStatus.CONFLICT.value(), status);
    }

    @Test
    @WithMockUser(roles = {"USER"}, username = "user1@email.com")
    public void getAllGroupIngredients_getAllIngredientsFromAGroupPositive_expectedSuccess() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/ingredients/{groupId}", 1))
            .andExpect(status().isOk()).andReturn();
        String contentResult = mvcResult.getResponse().getContentAsString();
        List<IngredientListDto> result = objectMapper.readValue(contentResult, new TypeReference<>() {
        });

        assertAll(
            () -> assertEquals(69, result.size()),
            () -> assertEquals("Advocaat", result.get(0).getName()),
            () -> assertEquals("Amaretto", result.get(1).getName()),
            () -> assertEquals("Anisette", result.get(2).getName())
        );
    }

    @Test
    @WithMockUser(roles = {"USER"}, username = "user1@email.com")
    public void getAllGroupIngredients_getAllIngredientsFromAGroupThatDoesNotExist_expectedNotFoundException() throws Exception {
        var response = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/ingredients/{groupId}", -1))
            .andExpect(status().isNotFound()).andReturn().getResponse();
        int status = response.getStatus();
        assertAll(
            () -> assertEquals(HttpStatus.NOT_FOUND.value(), status),
            () -> assertTrue(response.getContentAsString().contains("Group not found"))
        );
    }

    @Test
    @WithMockUser(roles = {"USER"}, username = "user1@email.com")
    @Transactional
    @Rollback
    public void searchAutocomplete_deleteAllIngredientsAndGetSuggestions_FindingResultsContainingRAndAllIngredientsDeleted() throws Exception {
        IngredientListDto[] preferences = new IngredientListDto[0];

        MvcResult mvcResultDeletePreferences = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/ingredients/user-ingredients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(preferences))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk()).andReturn();
        String contentResultDeleteIngredients = mvcResultDeletePreferences.getResponse().getContentAsString();
        List<PreferenceListDto> resultDeleteIngredients = objectMapper.readValue(contentResultDeleteIngredients, new TypeReference<>() {
        });


        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/ingredients/user-ingredients-auto/{ingredientsName}", "r"))
            .andExpect(status().isOk()).andReturn();
        String contentResult = mvcResult.getResponse().getContentAsString();
        List<PreferenceListDto> resultAutocomplete = objectMapper.readValue(contentResult, new TypeReference<>() {
        });

        assertAll(
            () -> assertTrue(resultAutocomplete.size() <= 10),
            () -> assertFalse(resultAutocomplete.isEmpty()),
            () -> assertTrue(resultAutocomplete.stream().allMatch(item -> item.getName().contains("r")),
                "All names should contain 'r'"),
            () -> assertEquals(0, resultDeleteIngredients.size())
        );

    }




}
