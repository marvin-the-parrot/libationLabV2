package at.ac.tuwien.sepr.groupphase.backend.repository.test;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.repository.MessageRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("generateData")
@SpringBootTest
public class MessageRepositoryTest {

    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    public void findAllByApplicationUserOrderByIsReadAscSentAtDesc_searchByUser_Expected1() {
        int expected = 1;
        ApplicationUser user = userRepository.findByEmail("user1@email.com");
        int result = messageRepository.findAllByApplicationUserOrderByIsReadAscSentAtDesc(user).size();

        assertEquals(expected, result);
    }

    @Test
    public void countByApplicationUserAndIsRead_searchByUserAndIsRead_Expected1() {
        int expected = 1;
        ApplicationUser user = userRepository.findByEmail("user1@email.com");
        int result = (int) messageRepository.countByApplicationUserAndIsRead(user, false);
        assertEquals(expected, result);
    }

    @Test
    public void findAllByApplicationUserAndGroupId_searchByUserAndGroupId_Expected1() {
        int expected = 1;
        ApplicationUser user = userRepository.findByEmail("user1@email.com");
        int result = messageRepository.findAllByApplicationUserAndGroupId(user, 3L).size();
        assertEquals(expected, result);
    }
}
