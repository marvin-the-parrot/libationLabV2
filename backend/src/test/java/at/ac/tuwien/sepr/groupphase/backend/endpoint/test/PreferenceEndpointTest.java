package at.ac.tuwien.sepr.groupphase.backend.endpoint.test;


import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PreferenceListDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles({"test", "generateData"})
@SpringBootTest
@EnableWebMvc
@WebAppConfiguration
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class PreferenceEndpointTest {

    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webAppContext;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
    }

    @Test
    @WithMockUser(roles = {"USER"}, username = "user1@email.com")
    public void searchAutocomplete_searchingForPreferencesPositive_FindingResultsContainingR() throws Exception{
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/preferences/user-preference-auto/{preferenceName}", "r"))
            .andExpect(status().isOk()).andReturn();
        String contentResult = mvcResult.getResponse().getContentAsString();
        List<PreferenceListDto> result = objectMapper.readValue(contentResult, new TypeReference<>() {
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
    public void searchAutocomplete_searchingForPreferencesNegative_userNotFound() throws Exception{
        var response = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/preferences/user-preference-auto/{preferenceName}", "r"))
            .andExpect(status().isNotFound()).andReturn().getResponse();
        int status = response.getStatus();
        assertEquals(HttpStatus.NOT_FOUND.value(), status);
    }

    @Test
    @WithMockUser(roles = {"USER"}, username = "user1@email.com")
    public void getUserPreferences_gettingUserPreferencesPositive_expectedSize() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/preferences/user-preferences"))
            .andExpect(status().isOk()).andReturn();
        String contentResult = mvcResult.getResponse().getContentAsString();
        List<PreferenceListDto> result = objectMapper.readValue(contentResult, new TypeReference<>() {
        });

        assertAll(
            () -> assertEquals(6, result.size())
        );
    }

    @Test
    @WithMockUser(roles = {"USER"}, username = "user1@wrongDomain.com")
    public void getUserPreferences_gettingUserPreferencesNegative_NotFoundException() throws Exception {
        var response = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/preferences/user-preferences"))
            .andExpect(status().isNotFound()).andReturn().getResponse();
        int status = response.getStatus();
        assertEquals(HttpStatus.NOT_FOUND.value(), status);
    }

    @Test
    @WithMockUser(roles = {"USER"}, username = "user1@email.com")
    @Transactional
    @Rollback
    public void addUserPreferences_addingPreferencesPositive_expectedSize() throws Exception {
        PreferenceListDto[] preferences = new PreferenceListDto[2];
        PreferenceListDto ginger = new PreferenceListDto();
        ginger.setId(67L);
        ginger.setName("Ginger");
        preferences[0] = ginger;
        PreferenceListDto cherry = new PreferenceListDto();
        cherry.setId(54L);
        cherry.setName("Cherry");
        preferences[1] = cherry;

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/preferences/user-preferences")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(preferences))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk()).andReturn();
        String contentResult = mvcResult.getResponse().getContentAsString();
        List<PreferenceListDto> result = objectMapper.readValue(contentResult, new TypeReference<>() {
        });

        assertAll(
            () -> assertEquals(2, result.size()),
            () -> assertEquals("Cherry", result.get(0).getName()),
            () -> assertEquals("Ginger", result.get(1).getName())
        );
    }

    @Test
    @WithMockUser(roles = {"USER"}, username = "user1@wrongDomain.com")
    public void addUserPreferences_addingPreferencesNegative_NotFoundExceptionBecauseUserNotExists() throws Exception {
        PreferenceListDto[] preferences = new PreferenceListDto[2];
        PreferenceListDto ginger = new PreferenceListDto();
        ginger.setId(67L);
        ginger.setName("Ginger");
        preferences[0] = ginger;
        PreferenceListDto cherry = new PreferenceListDto();
        cherry.setId(54L);
        cherry.setName("Cherry");
        preferences[1] = cherry;

        var response = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/preferences/user-preferences")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(preferences))
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
    public void addUserPreferences_addingPreferencesNegative_NotFoundExceptionBecausePreferenceDoesNotExist() throws Exception {
        PreferenceListDto[] preferences = new PreferenceListDto[2];
        PreferenceListDto ginger = new PreferenceListDto();
        ginger.setId(-1L);
        ginger.setName("Wrong Preference");
        preferences[0] = ginger;
        PreferenceListDto cherry = new PreferenceListDto();
        cherry.setId(48L);
        cherry.setName("Cherry");
        preferences[1] = cherry;

        var response = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/preferences/user-preferences")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(preferences))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound()).andReturn().getResponse();
        int status = response.getStatus();
        assertAll(
            () -> assertEquals(HttpStatus.NOT_FOUND.value(), status),
            () -> assertTrue(response.getContentAsString().contains("Preference not found"))
        );

    }

    @Test
    @WithMockUser(roles = {"USER"}, username = "user1@email.com")
    public void addUserPreferences_addingPreferencesNegative_ConflictExceptionPreferenceDoesHaveWrongID() throws Exception {
        PreferenceListDto[] preferences = new PreferenceListDto[2];
        PreferenceListDto ginger = new PreferenceListDto();
        ginger.setId(1L);
        ginger.setName("Wrong Preference");
        preferences[0] = ginger;
        PreferenceListDto cherry = new PreferenceListDto();
        cherry.setId(48L);
        cherry.setName("Cherry");
        preferences[1] = cherry;

        var response = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/preferences/user-preferences")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(preferences))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isConflict()).andReturn().getResponse();
        int status = response.getStatus();
        assertEquals(HttpStatus.CONFLICT.value(), status);
        assertAll(
            () -> assertEquals(HttpStatus.CONFLICT.value(), status),
            () -> assertTrue(response.getContentAsString().contains("Wrong Preference is not the same as Creamy"))
        );
    }

    @Test
    @WithMockUser(roles = {"USER"}, username = "user1@email.com")
    @Transactional
    @Rollback
    public void searchAutocomplete_deleteAllPreferencesAndGetSuggestions_FindingResultsContainingRAndAllPreferencesDeleted() throws Exception {
        PreferenceListDto[] preferences = new PreferenceListDto[0];

        MvcResult mvcResultDeletePreferences = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/preferences/user-preferences")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(preferences))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk()).andReturn();
        String contentResultDeletePreferences = mvcResultDeletePreferences.getResponse().getContentAsString();
        List<PreferenceListDto> resultDeletePreferences = objectMapper.readValue(contentResultDeletePreferences, new TypeReference<>() {
        });


        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/preferences/user-preference-auto/{preferenceName}", "r"))
            .andExpect(status().isOk()).andReturn();
        String contentResult = mvcResult.getResponse().getContentAsString();
        List<PreferenceListDto> resultAutocomplete = objectMapper.readValue(contentResult, new TypeReference<>() {
        });

        assertAll(
            () -> assertTrue(resultAutocomplete.size() <= 10),
            () -> assertFalse(resultAutocomplete.isEmpty()),
            () -> assertTrue(resultAutocomplete.stream().allMatch(item -> item.getName().contains("r")),
                "All names should contain 'r'"),
            () -> assertEquals(0, resultDeletePreferences.size())
        );

    }

}
