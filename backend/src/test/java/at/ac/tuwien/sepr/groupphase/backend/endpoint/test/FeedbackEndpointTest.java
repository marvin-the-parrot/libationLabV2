package at.ac.tuwien.sepr.groupphase.backend.endpoint.test;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CocktailFeedbackDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.FeedbackCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.FeedbackState;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.GroupDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MessageDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationGroup;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationMessage;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Cocktail;
import at.ac.tuwien.sepr.groupphase.backend.entity.Feedback;
import at.ac.tuwien.sepr.groupphase.backend.entity.FeedbackKey;
import at.ac.tuwien.sepr.groupphase.backend.repository.CocktailRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.FeedbackRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.MessageRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles({"test", "generateData"})
@SpringBootTest
@EnableWebMvc
@WebAppConfiguration
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FeedbackEndpointTest {

    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webAppContext;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private FeedbackRepository feedbackRepository;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private CocktailRepository cocktailRepository;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
    }

    @Test
    @WithMockUser(username = "user1@email.com")
    public void createFeedbackRelations_withValidData_expectSuccess() throws Exception {
        FeedbackCreateDto feedbackToCreate = new FeedbackCreateDto();
        feedbackToCreate.setGroupId(1L);
        feedbackToCreate.setCocktailIds(new Long[]{76L, 79L, 43L});

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/feedback/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(feedbackToCreate)))
            .andExpect(status().isCreated()).andReturn();

        // 3 cocktails * 3 users = 9 relations
        int expectedRelations = 9;
        int actualRelations = feedbackRepository.findAll().size();

        assertEquals(expectedRelations, actualRelations);
    }

    @Test
    @WithMockUser(username = "user1@email.com")
    public void createFeedbackRelations_withInvalidData_expectBadRequest() throws Exception {
        FeedbackCreateDto feedbackToCreate = new FeedbackCreateDto();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/feedback/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(feedbackToCreate)))
            .andExpect(status().isBadRequest()).andReturn();

        Map responseMap = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);

        assertEquals("No Feedback to create sent", responseMap.get("detail"));
    }

    @Test
    @WithMockUser(username = "user1@email.com")
    public void createFeedbackRelations_withInvalidData_expectNotFound() throws Exception {
        FeedbackCreateDto feedbackToCreate = new FeedbackCreateDto();
        feedbackToCreate.setGroupId(-99L);
        feedbackToCreate.setCocktailIds(new Long[]{76L, 79L, 43L});

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/feedback/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(feedbackToCreate)))
            .andExpect(status().isNotFound()).andReturn();

        Map responseMap = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);

        assertEquals("Group not found", responseMap.get("detail"));
    }

    @Test
    @WithMockUser(username = "user1@email.com")
    public void createFeedbackRelationsForNewUser_withValidData_expectSuccess() throws Exception {
        generateTestData();

        assertEquals(9, feedbackRepository.findAll().size());

        ApplicationMessage message = messageRepository.findById(1L).orElseThrow();

        GroupDetailDto group = new GroupDetailDto();
        group.setId(3L);
        group.setName("Group3");
        MessageDetailDto messageToAccept = new MessageDetailDto();
        messageToAccept.setId(message.getId());
        messageToAccept.setText(message.getText());
        messageToAccept.setGroup(group);
        messageToAccept.setIsRead(message.getIsRead());
        messageToAccept.setSentAt(message.getSentAt().toString());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/messages/accept")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(messageToAccept)))
            .andExpect(status().isCreated());

        assertEquals(12, feedbackRepository.findAll().size());
    }

    @Test
    @WithMockUser(username = "user7@email.com")
    public void updateRatings_withValidData_expectSuccess() throws Exception {
        generateTestData();

        CocktailFeedbackDto feedbackToUpdate = new CocktailFeedbackDto();
        feedbackToUpdate.setGroupId(3L);
        feedbackToUpdate.setCocktailId(76L);
        feedbackToUpdate.setRating(FeedbackState.Like);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/feedback/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(feedbackToUpdate)))
            .andExpect(status().isOk());

        FeedbackKey feedbackKey = new FeedbackKey(7L, 3L, 76L);
        Feedback feedback = feedbackRepository.findByFeedbackKey(feedbackKey);

        assertEquals(FeedbackState.Like, feedback.getRating());
    }

    @Test
    @WithMockUser(username = "user1@email.com")
    public void updateRatings_withInvalidGroup_expectNotFound() throws Exception {
        generateTestData();

        CocktailFeedbackDto feedbackToUpdate = new CocktailFeedbackDto();
        feedbackToUpdate.setGroupId(-99L);
        feedbackToUpdate.setCocktailId(76L);
        feedbackToUpdate.setRating(FeedbackState.Like);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/feedback/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(feedbackToUpdate)))
            .andExpect(status().isNotFound()).andReturn();

        Map responseMap = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);

        assertEquals("Group not found", responseMap.get("detail"));
    }

    @Test
    @WithMockUser(username = "user7@email.com")
    public void deleteFeedbackRelationsAtCocktailChange_withValidData_expectSuccess() throws Exception {
        generateTestData();

        ApplicationGroup group = groupRepository.findById(3L).orElseThrow();
        ApplicationUser user = userRepository.findByEmail("user7@email.com");

        assertEquals(9, feedbackRepository.findAll().size());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/feedback/delete/" + group.getId() + "/" + user.getId())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        assertEquals(6, feedbackRepository.findAll().size());
    }

    @Test
    @WithMockUser(username = "user1@email.com")
    public void deleteFeedbackRelationsAtCocktailChange_withInvalidGroup_expectNotFound() throws Exception {
        generateTestData();

        ApplicationUser user = userRepository.findByEmail("user1@email.com");

        assertEquals(9, feedbackRepository.findAll().size());

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/feedback/delete/" + -99L + "/" + user.getId())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound()).andReturn();

        Map responseMap = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);

        assertEquals("Group not found", responseMap.get("detail"));
    }

    private void generateTestData() throws Exception {
        ApplicationGroup group = groupRepository.findById(3L).orElseThrow();
        group.setCocktails(Set.of(
            cocktailRepository.findById(76L).orElseThrow(),
            cocktailRepository.findById(79L).orElseThrow(),
            cocktailRepository.findById(43L).orElseThrow()
        ));

        groupRepository.save(group);

        FeedbackCreateDto feedbackToCreate = new FeedbackCreateDto();
        feedbackToCreate.setGroupId(3L);
        feedbackToCreate.setCocktailIds(new Long[]{76L, 79L, 43L});

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/feedback/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(feedbackToCreate)))
            .andExpect(status().isCreated());
    }
}
