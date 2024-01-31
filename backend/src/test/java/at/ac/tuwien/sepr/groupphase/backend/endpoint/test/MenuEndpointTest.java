package at.ac.tuwien.sepr.groupphase.backend.endpoint.test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CocktailOverviewDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MenuCocktailsDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.RecommendedMenuesDto;
import io.jsonwebtoken.lang.Assert;
import jakarta.transaction.Transactional;

@ActiveProfiles({"test", "generateData"})
@SpringBootTest
public class MenuEndpointTest {

    @Autowired
    private WebApplicationContext webAppContext;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void getMenu_Expected1Menu() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/menu/1")).andExpect(status().isOk()).andReturn();
        String contentResult = mvcResult.getResponse().getContentAsString();
        MenuCocktailsDto result = objectMapper.readValue(contentResult, new TypeReference<MenuCocktailsDto>() {
        });
        assertAll(
            () -> Assert.eq(result.getGroupId(), 1L, "Group id of Menu."),
            () -> Assert.eq(result.getCocktailsList().size(), 0, "Number of cocktails in menu.")
        );
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void getMenu_ExpectedNotFoundException() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/menu/-1")).andExpect(status().isNotFound()).andReturn();
        String contentResult = mvcResult.getResponse().getContentAsString();

        assertEquals("Group with id -1 not found", contentResult);
    }

    @Test
    @WithMockUser(roles = {"USER"}, username = "user1@email.com")
    @Transactional
    @Rollback
    public void create_addNewMenu_expectedCreatedMenu() throws Exception {
    	CocktailOverviewDto cocktailOverviewDto = new CocktailOverviewDto();
    	cocktailOverviewDto.setId(1L);
    	cocktailOverviewDto.setName("Cocktial Name");
    	cocktailOverviewDto.setImagePath("path.image");
    	MenuCocktailsDto menuCocktailsDto = new MenuCocktailsDto();
    	menuCocktailsDto.setGroupId(1L);
    	menuCocktailsDto.setCocktailsList(List.of(cocktailOverviewDto));

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/menu")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(menuCocktailsDto))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated()).andReturn();
        String contentResult = mvcResult.getResponse().getContentAsString();
        MenuCocktailsDto result = objectMapper.readValue(contentResult, new TypeReference<MenuCocktailsDto>() {
        });
        assertAll(
            () -> assertEquals(1L, result.getGroupId()),
            () -> assertEquals(1, result.getCocktailsList().size())
        );
    }

    @Test
    @WithMockUser(roles = {"USER"}, username = "user1@email.com")
    @Transactional
    @Rollback
    public void create_addNewMenu_expectedGroupNotFoundException() throws Exception {
    	CocktailOverviewDto cocktailOverviewDto = new CocktailOverviewDto();
    	cocktailOverviewDto.setId(1L);
    	cocktailOverviewDto.setName("Cocktial Name");
    	cocktailOverviewDto.setImagePath("path.image");
    	MenuCocktailsDto menuCocktailsDto = new MenuCocktailsDto();
    	menuCocktailsDto.setGroupId(-1L);
    	menuCocktailsDto.setCocktailsList(List.of(cocktailOverviewDto));

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/menu")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(menuCocktailsDto))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound()).andReturn();
        String contentResult = mvcResult.getResponse().getContentAsString();

        assertEquals("Group with id -1 not found", contentResult);
    }

    @Test
    @WithMockUser(roles = {"USER"}, username = "user1@email.com")
    @Transactional
    @Rollback
    public void create_addNewMenu_expectedCocktailNotFoundException() throws Exception {
    	CocktailOverviewDto cocktailOverviewDto = new CocktailOverviewDto();
    	cocktailOverviewDto.setId(-11L);
    	cocktailOverviewDto.setName("Cocktial Name");
    	cocktailOverviewDto.setImagePath("path.image");
    	MenuCocktailsDto menuCocktailsDto = new MenuCocktailsDto();
    	menuCocktailsDto.setGroupId(1L);
    	menuCocktailsDto.setCocktailsList(List.of(cocktailOverviewDto));

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/menu")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(menuCocktailsDto))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isConflict()).andReturn();
        String contentResult = mvcResult.getResponse().getContentAsString();

        assertEquals("{\"message\":\"CONFLICT: Not all cocktails found\",\"errors\":[\"the given cocktails do not match the ones found\"]}", contentResult);
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void getSuggestionForCocktails_Expected2Menus() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/menu/1/recommendation?numberOfCocktails=4")).andExpect(status().isOk()).andReturn();
        String contentResult = mvcResult.getResponse().getContentAsString();
        RecommendedMenuesDto result = objectMapper.readValue(contentResult, new TypeReference<RecommendedMenuesDto>() {
        });
        assertAll(
            () -> Assert.gt(result.getMenuList().size(), 1, "Number of recommendations not greater than to 2 although it is expected to be."),
            () -> Assert.gt(result.getMenuList().get(0).getLv(), 1.0f, "Libation Value of first recommendation not greater than 1 although it is expected to be."),
            () -> Assert.eq(result.getMenuList().get(0).getCocktailMenu().size(),4, "Number of cocktails in first recommendation ")
        );


    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void getSuggestionForCocktailsMenuSize0_ExpectedException() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/menu/1/recommendation?numberOfCocktails=0")).andExpect(status().isBadRequest());

    }


}
