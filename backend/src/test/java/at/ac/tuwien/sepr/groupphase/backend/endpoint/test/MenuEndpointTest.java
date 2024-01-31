package at.ac.tuwien.sepr.groupphase.backend.endpoint.test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;
import java.util.Set;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CocktailFeedbackDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.FeedbackCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.FeedbackState;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.IngredientListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MenuCocktailsDetailViewDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MenuCocktailsDetailViewHostDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationGroup;
import at.ac.tuwien.sepr.groupphase.backend.entity.Cocktail;
import at.ac.tuwien.sepr.groupphase.backend.repository.CocktailRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.MenuService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
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
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class MenuEndpointTest {

    @Autowired
    private WebApplicationContext webAppContext;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private CocktailRepository cocktailRepository;
    @Autowired
    private MenuService menuService;
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
    @WithMockUser(username = "user1@email.com")
    public void getRatings_withValidData_ExpectedMenuWithRatings() throws Exception {
        createRatings();

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/menu/1/detail/host"))
            .andExpect(status().isOk()).andReturn();
        String contentResult = mvcResult.getResponse().getContentAsString();
        MenuCocktailsDetailViewHostDto result = objectMapper.readValue(contentResult, new TypeReference<MenuCocktailsDetailViewHostDto>() {
        });

        assertAll(
            () -> Assert.eq(result.getCocktailsList()[0].getPositiveRating(), 0, "First cocktail should have 0 positive rating"),
            () -> Assert.eq(result.getCocktailsList()[0].getNegativeRating(), 0, "First cocktail should have 0 negative rating"),
            () -> Assert.eq(result.getCocktailsList()[1].getPositiveRating(), 1, "Second cocktail should have 1 positive rating"),
            () -> Assert.eq(result.getCocktailsList()[1].getNegativeRating(), 0, "Second cocktail should have 0 negative rating"),
            () -> Assert.eq(result.getCocktailsList()[2].getPositiveRating(), 0, "Third cocktail should have 0 positive rating"),
            () -> Assert.eq(result.getCocktailsList()[2].getNegativeRating(), 1, "Third cocktail should have 1 negative rating")
        );
    }

    @Test
    @WithMockUser(username = "user1@email.com")
    public void updateMixableCocktails_withValidData_ExpectedSuccess() throws Exception {
        createMenu();

        MenuCocktailsDto menuCocktailsDtoOld = menuService.findMenuOfGroup(1L);

        IngredientListDto[] ingredients = new IngredientListDto[2];
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/menu")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(ingredients)))
            .andExpect(status().isOk());

        MenuCocktailsDto menuCocktailsDtoResult = menuService.findMenuOfGroup(1L);

        assertEquals(menuCocktailsDtoOld.getCocktailsList().size() - 2, menuCocktailsDtoResult.getCocktailsList().size());
    }

    @Test
    @WithMockUser(username = "user1@email.com")
    public void getMenuDetail_withValidData_ExpectedMenuCocktailsDetailViewDto() throws Exception {
        createMenu();
        createRatings();

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/menu/1/detail"))
            .andExpect(status().isOk()).andReturn();
        String contentResult = mvcResult.getResponse().getContentAsString();
        MenuCocktailsDetailViewDto result = objectMapper.readValue(contentResult, new TypeReference<MenuCocktailsDetailViewDto>() {
        });

        assertAll(
            () -> Assert.eq(result.getCocktailsList()[0].getId(), 43L, "First cocktail should have id 43"),
            () -> Assert.eq(result.getCocktailsList()[1].getId(), 76L, "First cocktail should have id 76"),
            () -> Assert.eq(result.getCocktailsList()[2].getId(), 79L, "First cocktail should have id 79")
        );
    }

    @Test
    @WithMockUser(username = "user1@email.com")
    public void getMenuDetail_withInvalidGroup_ExpectedNotFoundException() throws Exception {
        createMenu();
        createRatings();

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/menu/-99/detail"))
            .andExpect(status().isNotFound()).andReturn();

        Map responseMap = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Map.class);

        assertEquals("Group not found", responseMap.get("detail"));
    }

    private void createRatings() throws Exception {
        ApplicationGroup group = groupRepository.findById(1L).orElseThrow();
        group.setCocktails(Set.of(
            cocktailRepository.findById(76L).orElseThrow(),
            cocktailRepository.findById(79L).orElseThrow(),
            cocktailRepository.findById(43L).orElseThrow()
        ));

        groupRepository.save(group);

        FeedbackCreateDto feedbackToCreate = new FeedbackCreateDto();
        feedbackToCreate.setGroupId(1L);
        feedbackToCreate.setCocktailIds(new Long[]{76L, 79L, 43L});

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/feedback/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(feedbackToCreate)))
            .andExpect(status().isCreated());

        CocktailFeedbackDto feedbackToUpdate = new CocktailFeedbackDto();
        feedbackToUpdate.setCocktailId(76L);
        feedbackToUpdate.setGroupId(1L);
        feedbackToUpdate.setRating(FeedbackState.Like);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/feedback/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(feedbackToUpdate)))
            .andExpect(status().isOk());

        feedbackToUpdate.setCocktailId(79L);
        feedbackToUpdate.setGroupId(1L);
        feedbackToUpdate.setRating(FeedbackState.Dislike);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/feedback/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(feedbackToUpdate)))
            .andExpect(status().isOk());
    }

    private void createMenu() throws Exception {
        List<Long> cocktailIds = List.of(76L, 79L, 43L);
        List<Cocktail> cocktails = cocktailRepository.findByIdIn(cocktailIds).stream().toList();
        MenuCocktailsDto menuToCreate = new MenuCocktailsDto();
        menuToCreate.setGroupId(1L);
        menuToCreate.setCocktailsList(List.of(
            new CocktailOverviewDto(cocktails.get(0).getId(), cocktails.get(0).getName(), cocktails.get(0).getImagePath()),
            new CocktailOverviewDto(cocktails.get(1).getId(), cocktails.get(1).getName(), cocktails.get(1).getImagePath()),
            new CocktailOverviewDto(cocktails.get(2).getId(), cocktails.get(2).getName(), cocktails.get(2).getImagePath())
        ));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/menu")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(menuToCreate)))
            .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void getSuggestionForCocktailsMenuSize0_ExpectedException() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/menu/1/recommendation?numberOfCocktails=0")).andExpect(status().isBadRequest());

    }


}
