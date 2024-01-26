package at.ac.tuwien.sepr.groupphase.backend.endpoint.test;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.GroupDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MessageCountDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MessageCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MessageDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MessageSetReadDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationMessage;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles({"test", "generateData"})
@SpringBootTest
@EnableWebMvc
@WebAppConfiguration
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
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
    @Rollback
    @Transactional
    public void createMessage_createMessageWithValidData_expectedTrue() throws Exception {
        long unreadMessagesBefore = messageRepository.countByApplicationUserAndIsRead(userRepository.findByEmail("user1@email.com"), false);

        MessageCreateDto message = new MessageCreateDto();
        message.setUserId(1L);
        message.setGroupId(4L);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/messages/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(message)))
            .andExpect(status().isCreated()).andReturn();

        long unreadMessagesAfter = messageRepository.countByApplicationUserAndIsRead(userRepository.findByEmail("user1@email.com"), false);

        assertEquals(unreadMessagesBefore + 1, unreadMessagesAfter);
    }

    @Test
    @Rollback
    @Transactional
    public void createMessage_createMessageWithNotExistingGroup_expectedNotFoundException() throws Exception {
        MessageCreateDto message = new MessageCreateDto();
        message.setUserId(1L);
        message.setGroupId(-6L);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/messages/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(message)))
            .andExpect(status().isNotFound()).andReturn();

        Map responseMap = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);

        assertEquals("Could not find group", responseMap.get("detail"));
    }

    @Test
    @Rollback
    @Transactional
    public void createMessage_createMessageWithUserAlreadyInGroup_expectedValidationException() throws Exception {
        MessageCreateDto message = new MessageCreateDto();
        message.setUserId(1L);
        message.setGroupId(1L);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/messages/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(message)))
            .andExpect(status().isUnprocessableEntity()).andReturn();

        Map responseMap = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);

        assertEquals("Validation of message for create failed. Failed validations: User User1 is already in the group.", responseMap.get("detail"));
    }

    @Test
    @Rollback
    @Transactional
    public void createMessage_createMessageWithUserAlreadyInvited_expectedValidationException() throws Exception {
        MessageCreateDto message = new MessageCreateDto();
        message.setUserId(1L);
        message.setGroupId(3L);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/messages/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(message)))
            .andExpect(status().isUnprocessableEntity()).andReturn();

        Map responseMap = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);

        assertEquals("Validation of message for create failed. Failed validations: You already invited user User1 to the group.", responseMap.get("detail"));
    }

    @Test
    @Rollback
    @Transactional
    @WithMockUser(username = "user1@email.com")
    public void acceptGroupInvitation_acceptGroupInvitationWithValidData_expectedTrue() throws Exception {
        GroupDetailDto group = new GroupDetailDto();
        group.setId(3L);
        group.setName("Group3");

        MessageDetailDto message = new MessageDetailDto();
        message.setId(1L);
        message.setGroup(group);
        message.setIsRead(true);
        message.setSentAt(LocalDateTime.now().toString());
        message.setText("You were invited to drink with Group3");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/messages/accept")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(message)))
            .andExpect(status().isCreated()).andReturn();
    }

    @Test
    @Rollback
    @Transactional
    @WithMockUser(username = "user1@email.com")
    public void acceptGroupInvitation_acceptGroupInvitationWithNotExistingMessage_expectedNotFoundException() throws Exception {
        GroupDetailDto group = new GroupDetailDto();
        group.setId(3L);
        group.setName("Group3");

        LocalDateTime localDateTime = LocalDateTime.now();

        MessageDetailDto message = new MessageDetailDto();
        message.setId(-1L);
        message.setGroup(group);
        message.setIsRead(true);
        message.setSentAt(localDateTime.toString());
        message.setText("You were invited to drink with Group3");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/messages/accept")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(message)))
            .andExpect(status().isNotFound()).andReturn();

        Map responseMap = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);

        assertEquals("Could not find message with id " + message.getId(), responseMap.get("detail"));
    }

    @Test
    @Rollback
    @Transactional
    @WithMockUser(username = "user1@email.com")
    public void acceptGroupInvitation_acceptGroupInvitationWithNotExistingGroup_expectedNotFoundException() throws Exception {
        GroupDetailDto group = new GroupDetailDto();
        group.setId(-1L);
        group.setName("Group1");

        MessageDetailDto message = new MessageDetailDto();
        message.setId(1L);
        message.setGroup(group);
        message.setIsRead(true);
        message.setSentAt(LocalDateTime.now().toString());
        message.setText("You were invited to drink with Group1");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/messages/accept")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(message)))
            .andExpect(status().isNotFound()).andReturn();

        Map responseMap = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);

        assertEquals("Could not find group", responseMap.get("detail"));
    }

    @Test
    @Rollback
    @Transactional
    @WithMockUser(username = "user1@email.com")
    public void updateMessage_updateMessageWithValidData_expectedUpdatedMessage() throws Exception {
        GroupDetailDto group = new GroupDetailDto();
        group.setId(3L);
        group.setName("Group3");

        MessageDetailDto message = new MessageDetailDto();
        message.setId(1L);
        message.setGroup(group);
        message.setIsRead(true);
        message.setSentAt(LocalDateTime.now().toString());
        message.setText("You were invited to drink with Group3");

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/messages/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(message)))
            .andExpect(status().isOk()).andReturn();
    }

    @Test
    @Rollback
    @Transactional
    @WithMockUser(username = "user1@email.com")
    public void updateMessage_updateMessageWithNotExistingMessage_expectedNotFoundException() throws Exception {
        GroupDetailDto group = new GroupDetailDto();
        group.setId(3L);
        group.setName("Group3");

        LocalDateTime localDateTime = LocalDateTime.now();

        MessageDetailDto message = new MessageDetailDto();
        message.setId(-1L);
        message.setGroup(group);
        message.setIsRead(true);
        message.setSentAt(localDateTime.toString());
        message.setText("You were invited to drink with Group3");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/messages/{id}", -1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(message)))
            .andExpect(status().isNotFound()).andReturn();

        Map responseMap = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);

        assertEquals("Could not find message from group Group3 sent at " + localDateTime, responseMap.get("detail"));
    }

    @Test
    @Rollback
    @Transactional
    @WithMockUser(username = "user1@email.com")
    public void updateMessage_updateMessageWithNotExistingGroup_expectedNotFoundException() throws Exception {
        GroupDetailDto group = new GroupDetailDto();
        group.setId(-1L);
        group.setName("Group1");

        MessageDetailDto message = new MessageDetailDto();
        message.setId(1L);
        message.setGroup(group);
        message.setIsRead(true);
        message.setSentAt(LocalDateTime.now().toString());
        message.setText("You were invited to drink with Group1");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/messages/{id}", -1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(message)))
            .andExpect(status().isNotFound()).andReturn();

        Map responseMap = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);

        assertEquals("Could not find group", responseMap.get("detail"));
    }

    @Test
    @WithMockUser(username = "user1@email.com")
    public void deleteMessage_deleteMessageWithExistingId_expectedTrue() throws Exception {
        int expected = messageRepository.findAllByApplicationUserOrderByIsReadAscSentAtDesc(userRepository.findByEmail("user1@email.com")).size();
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/messages/{0}", 0L)).andExpect(status().isNotFound());
        int result = messageRepository.findAllByApplicationUserOrderByIsReadAscSentAtDesc(userRepository.findByEmail("user1@email.com")).size();
        assertEquals(expected, result);
    }

    @Test
    @WithMockUser(username = "user1@email.com")
    public void deleteMessage_deleteMessageWithNotExistingId_expectedTrue() throws Exception {
        int expected = messageRepository.findAll().size();
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/messages/{0}", -60L)).andExpect(status().isNotFound());
        int result = messageRepository.findAll().size();
        assertEquals(expected, result);
    }

    @Test
    @WithMockUser(username = "user1@email.com")
    public void markAllAsRead_markAllAsReadWithValidData_expectedTrue() throws Exception {
        int expectedReadMessagesSize = 1;
        MessageSetReadDto[] messagesToSetRead = new MessageSetReadDto[1];
        messagesToSetRead[0] = new MessageSetReadDto();
        messagesToSetRead[0].setRead(true);
        messagesToSetRead[0].setId(1L);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/messages/read")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(messagesToSetRead)))
            .andExpect(status().isOk());

        List<ApplicationMessage> messages = messageRepository.findAllByApplicationUserOrderByIsReadAscSentAtDesc(userRepository.findByEmail("user1@email.com"));
        int result = 0;
        for (ApplicationMessage message : messages) {
            if (message.getIsRead()) {
                result++;
            }
        }
        assertEquals(expectedReadMessagesSize, result);
    }

    @Test
    @WithMockUser(username = "user1@email.com")
    public void markAllAsRead_markAllAsReadWithInvalidData_expectedNotFound() throws Exception {
        MessageSetReadDto[] messagesToSetRead = new MessageSetReadDto[1];
        messagesToSetRead[0] = new MessageSetReadDto();
        messagesToSetRead[0].setRead(true);
        messagesToSetRead[0].setId(-1L);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/messages/read")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(messagesToSetRead)))
            .andExpect(status().isNotFound()).andReturn();

        Map responseMap = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);

        assertEquals("Could not find messages", responseMap.get("detail"));
    }
}
