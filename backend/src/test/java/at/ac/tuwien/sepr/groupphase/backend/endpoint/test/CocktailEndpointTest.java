package at.ac.tuwien.sepr.groupphase.backend.endpoint.test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CocktailDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.IngredientListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PreferenceListDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CocktailListDto;

@ActiveProfiles({"test", "generateData"})
@SpringBootTest
public class CocktailEndpointTest {

    @Autowired
    private WebApplicationContext webAppContext;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void searchCocktails_searchByIngredientAndCocktailName_Expected1() throws Exception {
        int expected = 1;
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/cocktails?cocktailName=Mojito&ingredientsName=Lime", "Mojito", "Lime")).andExpect(status().isOk()).andReturn();
        String contentResult = mvcResult.getResponse().getContentAsString();
        int result = objectMapper.readValue(contentResult, new TypeReference<List<CocktailListDto>>() {
        }).size();

        assertEquals(expected, result);
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void searchCocktails_searchByCocktailName_Expected1() throws Exception {
        int expected = 1;
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/cocktails?cocktailName=Mojito", "Mojito")).andExpect(status().isOk()).andReturn();
        String contentResult = mvcResult.getResponse().getContentAsString();
        int result = objectMapper.readValue(contentResult, new TypeReference<List<CocktailListDto>>() {
        }).size();

        assertEquals(expected, result);
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void getCocktailById_getCocktailByIdPositive_ExpectedCorrectCocktail() throws Exception {
        int expected = 1;
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/cocktails/{id}", "1")).andExpect(status().isOk()).andReturn();
        String contentResult = mvcResult.getResponse().getContentAsString();
        CocktailDetailDto result = objectMapper.readValue(contentResult, new TypeReference<>() {
        });

        assertAll(
                () -> assertEquals(expected, result.getId()),
                () -> assertEquals("Mojito", result.getName())
        );
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void getCocktailById_getCocktailByIdNegative_ExpectedNotFoundException() throws Exception {
        var response = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/cocktails/{id}", "-1")).andExpect(status().isNotFound()).andReturn().getResponse();

        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus()),
                () -> assertTrue(response.getContentAsString().contains("Cocktail with id -1 not found"))
        );
    }


    @Test
    @WithMockUser(roles = {"USER"})
    public void searchIngredientsAuto_searchIngredientsAutoPositive_ExpectedCorrectIngredients() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/cocktails/cocktail-ingredients-auto/{ingredientsName}", "r"))
            .andExpect(status().isOk()).andReturn();
        String contentResult = mvcResult.getResponse().getContentAsString();
        List<IngredientListDto> result = objectMapper.readValue(contentResult, new TypeReference<>() {
        });

        assertAll(
            () -> assertTrue(result.size() <= 10),
            () -> assertFalse(result.isEmpty()),
            () -> assertTrue(result.stream().allMatch(item -> item.getName().contains("r")),
                "All names should contain 'r'"),
            () -> assertEquals("151 Proof Rum", result.get(0).getName()),
            () -> assertEquals("Amaretto", result.get(1).getName()),
            () -> assertEquals("Angostura Bitters", result.get(2).getName()),
            () -> assertEquals("Aperol", result.get(3).getName()),
            () -> assertEquals("Apple Brandy", result.get(4).getName()),
            () -> assertEquals("Apricot Brandy", result.get(5).getName()),
            () -> assertEquals("Baileys Irish Cream", result.get(6).getName()),
            () -> assertEquals("Banana Liqueur", result.get(7).getName()),
            () -> assertEquals("Beer", result.get(8).getName()),
            () -> assertEquals("Bitters", result.get(9).getName())
        );
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void searchPreferencesAuto_searchPreferencesAutoPositive_ExpectedCorrectPreferences() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/cocktails/cocktail-preferences-auto/{preferenceName}", "r"))
            .andExpect(status().isOk()).andReturn();
        String contentResult = mvcResult.getResponse().getContentAsString();
        List<PreferenceListDto> result = objectMapper.readValue(contentResult, new TypeReference<>() {
        });

        assertAll(
            () -> assertTrue(result.size() <= 10),
            () -> assertFalse(result.isEmpty()),
            () -> assertTrue(result.stream().allMatch(item -> item.getName().contains("r")),
                "All names should contain 'r'"),
            () -> assertEquals("Amaretto", result.get(0).getName()),
            () -> assertEquals("Aperol", result.get(1).getName()),
            () -> assertEquals("Aromatic", result.get(2).getName()),
            () -> assertEquals("Beer Mug", result.get(3).getName()),
            () -> assertEquals("Beer Pilsner", result.get(4).getName()),
            () -> assertEquals("Berry", result.get(5).getName()),
            () -> assertEquals("Bitter", result.get(6).getName()),
            () -> assertEquals("Bourbon", result.get(7).getName()),
            () -> assertEquals("Brandy", result.get(8).getName()),
            () -> assertEquals("Brandy Snifter", result.get(9).getName())
        );
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void searchCocktails_searchCocktailsByName_ExpectedEmptyResult() throws Exception {
        int expected = 0;
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/cocktails?cocktailName=masdsadasd", "masdsadasd")).andExpect(status().isOk()).andReturn();
        String contentResult = mvcResult.getResponse().getContentAsString();
        int result = objectMapper.readValue(contentResult, new TypeReference<List<CocktailListDto>>() {
        }).size();

        assertEquals(expected, result);
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void searchCocktails_searchCocktailsByIngredientsAndName_ExpectedEmptyResult() throws Exception {
        int expected = 0;
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/cocktails?cocktailName=a&ingredientsName=Vodka,Anis", "a", "Vodka,Anis")).andExpect(status().isOk()).andReturn();
        String contentResult = mvcResult.getResponse().getContentAsString();
        int result = objectMapper.readValue(contentResult, new TypeReference<List<CocktailListDto>>() {
        }).size();

        assertEquals(expected, result);
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void searchCocktails_searchCocktailsByPreferencesAndName_ExpectedEmptyResult() throws Exception {
        int expected = 0;
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/cocktails?cocktailName=a&preferenceName=Vodka,Sour,Bitter", "a" ,"Vodka,Sour,Bitter")).andExpect(status().isOk()).andReturn();
        String contentResult = mvcResult.getResponse().getContentAsString();
        int result = objectMapper.readValue(contentResult, new TypeReference<List<CocktailListDto>>() {
        }).size();

        assertEquals(expected, result);
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void searchCocktails_searchCocktailsByPreferences_ExpectedFiveResults() throws Exception {
        int expected = 5;
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/cocktails?preferenceName=Anise", "Anise")).andExpect(status().isOk()).andReturn();
        String contentResult = mvcResult.getResponse().getContentAsString();
        int result = objectMapper.readValue(contentResult, new TypeReference<List<CocktailListDto>>() {
        }).size();

        assertEquals(expected, result);
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void searchCocktails_searchCocktailsByPreferences_ExpectedEmptyResult() throws Exception {
        int expected = 0;
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/cocktails?preferenceName=Anise,Almond", "Anise,Almond")).andExpect(status().isOk()).andReturn();
        String contentResult = mvcResult.getResponse().getContentAsString();
        int result = objectMapper.readValue(contentResult, new TypeReference<List<CocktailListDto>>() {
        }).size();

        assertEquals(expected, result);
    }


}
