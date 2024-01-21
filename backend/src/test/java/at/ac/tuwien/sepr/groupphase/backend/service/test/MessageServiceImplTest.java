package at.ac.tuwien.sepr.groupphase.backend.service.test;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.GroupDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MessageCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MessageDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationMessage;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.SimpleMessageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles({"test", "generateData"})
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class MessageServiceImplTest {

    @Autowired
    private SimpleMessageService messageServiceImpl;

    @Test
    @WithMockUser(username = "user1@email.com")
    public void getUnreadMessageCount_getUnreadMessageCountFromLoggedInUser_expected1() {
        long expected = 1;
        long result = messageServiceImpl.getUnreadMessageCount();
        assertEquals(expected, result);
    }

    @Test
    @WithMockUser(username = "user1@email.com")
    public void findAll_findAllMessagesFromLoggedInUser_expected1() {
        long expected = 1;
        long result = messageServiceImpl.findAll().size();
        assertEquals(expected, result);
    }

    @Test
    public void create_createMessageWithValidData_expectedMessage() {
        MessageCreateDto messageToCreate = new MessageCreateDto();
        messageToCreate.setGroupId(4L);
        messageToCreate.setUserId(1L);

        ApplicationMessage messageResult = assertDoesNotThrow(() -> messageServiceImpl.create(messageToCreate));

        assertEquals(messageToCreate.getGroupId(), messageResult.getGroupId());
        assertEquals(messageToCreate.getUserId(), messageResult.getApplicationUser().getId());
    }

    @Test
    public void create_createMessageWithInvalidGroupId_expectedNotFoundException() {
        MessageCreateDto messageToCreate = new MessageCreateDto();
        messageToCreate.setGroupId(100L);
        messageToCreate.setUserId(1L);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> messageServiceImpl.create(messageToCreate));
        assertEquals("Could not find group", exception.getMessage());
    }

    @Test
    public void create_createMessageWithInvalidUserId_expectedNotFoundException() {
        MessageCreateDto messageToCreate = new MessageCreateDto();
        messageToCreate.setGroupId(4L);
        messageToCreate.setUserId(-1L);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> messageServiceImpl.create(messageToCreate));
        assertEquals("Could not find the user", exception.getMessage());
    }

    @Test
    public void create_createMessageWithUserAlreadyInGroup_expectedValidationException() {
        MessageCreateDto messageToCreate = new MessageCreateDto();
        messageToCreate.setGroupId(1L);
        messageToCreate.setUserId(1L);

        ValidationException exception = assertThrows(ValidationException.class, () -> messageServiceImpl.create(messageToCreate));
        assertEquals("Validation of message for create failed. Failed validations: User User1 is already in the group.", exception.getMessage());
    }

    @Test
    public void create_createMessageWithUserAlreadyInvited_expectedValidationException2() {
        MessageCreateDto messageToCreate = new MessageCreateDto();
        messageToCreate.setGroupId(3L);
        messageToCreate.setUserId(1L);

        ValidationException exception = assertThrows(ValidationException.class, () -> messageServiceImpl.create(messageToCreate));
        assertEquals("Validation of message for create failed. Failed validations: You already invited user User1 to the group.", exception.getMessage());
    }

    @Test
    public void update_updateMessageWithValidData_expectedUpdatedMessage() {
        GroupDetailDto group = new GroupDetailDto();
        group.setId(3L);
        group.setName("Group3");

        MessageDetailDto messageToUpdate = new MessageDetailDto();
        messageToUpdate.setId(1L);
        messageToUpdate.setGroup(group);
        messageToUpdate.setIsRead(true);
        messageToUpdate.setSentAt(LocalDateTime.now().toString());
        messageToUpdate.setText("You were invited to drink with Group3");

        ApplicationMessage messageResult = assertDoesNotThrow(() -> messageServiceImpl.update(messageToUpdate));

        assertEquals(messageToUpdate.getId(), messageResult.getId());
        assertEquals(messageToUpdate.getText(), messageResult.getText());
        assertEquals(messageToUpdate.getGroup().getId(), messageResult.getGroupId());
        assertTrue(messageResult.getIsRead());
    }

    @Test
    public void update_updateMessageWithNotExistingMessage_expectedNotFoundException() {
        GroupDetailDto group = new GroupDetailDto();
        group.setId(3L);
        group.setName("Group3");

        MessageDetailDto messageToUpdate = new MessageDetailDto();
        messageToUpdate.setId(-1L);
        messageToUpdate.setGroup(group);
        messageToUpdate.setIsRead(true);
        messageToUpdate.setSentAt(LocalDateTime.now().toString());
        messageToUpdate.setText("You were invited to drink with Group3");

        NotFoundException exception = assertThrows(NotFoundException.class, () -> messageServiceImpl.update(messageToUpdate));
        assertEquals("Could not find message from group Group3 sent at " + messageToUpdate.getSentAt(), exception.getMessage());
    }

    @Test
    public void update_updateMessageWithNotExistingGroup_expectedNotFoundException() {
        GroupDetailDto group = new GroupDetailDto();
        group.setId(-1L);
        group.setName("Group1");

        MessageDetailDto messageToUpdate = new MessageDetailDto();
        messageToUpdate.setId(1L);
        messageToUpdate.setGroup(group);
        messageToUpdate.setIsRead(true);
        messageToUpdate.setSentAt(LocalDateTime.now().toString());
        messageToUpdate.setText("You were invited to drink with Group1");

        NotFoundException exception = assertThrows(NotFoundException.class, () -> messageServiceImpl.update(messageToUpdate));
        assertEquals("Could not find group", exception.getMessage());
    }

    @Test
    @WithMockUser(username = "user1@email.com")
    public void delete_deleteMessageWithExistingId_expectedTrue() {
        int expected = messageServiceImpl.findAll().size() - 1;
        messageServiceImpl.delete(1L);
        int result = messageServiceImpl.findAll().size();
        assertEquals(expected, result);
    }

    @Test
    public void delete_deleteMessageWithNotExistingId_expectedNotFoundException() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> messageServiceImpl.delete(-1L));
        assertEquals("Could not find message with id -1", exception.getMessage());
    }
}
