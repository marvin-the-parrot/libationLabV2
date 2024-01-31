package at.ac.tuwien.sepr.groupphase.backend.service.test;

import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserGroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.CustomUserDetailService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ActiveProfiles({"test", "generateData"})
@SpringBootTest

public class CustomUserDetailServiceTest {

    @Autowired
    private CustomUserDetailService customUserDetailService;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private UserGroupRepository userGroupRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    public void findUsersByGroup_withValidGroup_expectedSuccess() {
        var group = groupRepository.findById(1L).orElse(null);
        assertNotNull(group);

        var users = assertDoesNotThrow(() -> customUserDetailService.findUsersByGroup(group));
        assertEquals(3, users.size());
        assertEquals("User1", users.get(0).getName());
        assertEquals("User3", users.get(1).getName());
        assertEquals("User4", users.get(2).getName());
    }

    @Test
    @Transactional
    @WithMockUser(username = "user1@email.com")
    public void deleteUserByEmail_withValidEmail_expectedSuccess() {
        var group = groupRepository.findById(1L).orElse(null);
        assertNotNull(group);
        var members = userGroupRepository.findAllByApplicationGroup(group);
        assertEquals(3, members.size());

        var user = userRepository.findById(1L).orElse(null);
        assertNotNull(user);
        assertEquals("User1", user.getName());

        assertDoesNotThrow(() -> customUserDetailService.deleteUserByEmail());

        assertNull(userRepository.findById(1L).orElse(null));

        members = userGroupRepository.findAllByApplicationGroup(group);
        assertEquals(2, members.size());
    }
}
