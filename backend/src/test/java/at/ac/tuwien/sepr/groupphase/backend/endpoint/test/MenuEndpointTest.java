package at.ac.tuwien.sepr.groupphase.backend.endpoint.test;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CocktailListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.RecommendedMenuesDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.lang.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("generateData")
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
    public void getSuggestionForCocktails_Expected2Menus() throws Exception {
        int expected = 4;
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

}
