package at.ac.tuwien.sepr.groupphase.backend.endpoint.test;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MessageCountDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MessageDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import at.ac.tuwien.sepr.groupphase.backend.repository.MessageRepository;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("generateData")
@SpringBootTest
@EnableWebMvc
@WebAppConfiguration
public class MessageEndpointTest {

    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webAppContext;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
    }

    @Test
    @WithMockUser(username = "user1@email.com")
    public void countByApplicationUserAndIsRead_countingUnreadMessagesOfUserWithIdOne_expected1() throws Exception {
        long expected = 1;
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/messages/count")).andExpect(status().isOk()).andReturn();
        String contentResult = mvcResult.getResponse().getContentAsString();
        long result = objectMapper.readValue(contentResult, new TypeReference<MessageCountDto>() {
        }).getCount();
        assertEquals(expected, result);
    }

    @Test
    @WithMockUser(username = "user1@email.com")
    public void findAllByApplicationUserOrderByIsReadAscSentAtDesc_findingAllMessagesOfUserWithIdOne_expected1() throws Exception {
        int expected = 1;
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/messages")).andExpect(status().isOk()).andReturn();
        String contentResult = mvcResult.getResponse().getContentAsString();
        int result = objectMapper.readValue(contentResult, new TypeReference<List<MessageDetailDto>>() {
        }).size();
        assertEquals(expected, result);
    }

    @Test
    @WithMockUser(username = "user1@email.com")
    public void deleteMessage_delteMessageByExistingId_expectedTrue() throws Exception {
        int expected = messageRepository.findAllByApplicationUserOrderByIsReadAscSentAtDesc(userRepository.findByEmail("user1@email.com")).size();
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/messages/{0}", 0L)).andExpect(status().isNotFound());
        int result = messageRepository.findAllByApplicationUserOrderByIsReadAscSentAtDesc(userRepository.findByEmail("user1@email.com")).size();
        assertEquals(expected, result);
    }
}
