package at.ac.tuwien.sepr.groupphase.backend.repository.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationGroup;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.UserGroup;
import at.ac.tuwien.sepr.groupphase.backend.entity.UserGroupKey;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserGroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("generateData")
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserGroupRepositoryTest {

    @Autowired
    private UserGroupRepository userGroupRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    private ApplicationGroup applicationGroup;

    private ApplicationUser applicationUser;

    private UserGroup userGroup;

    private UserGroupKey userGroupKey;

    @BeforeEach
    public void setUp() {
        //userGroupRepository.deleteAll();
        applicationGroup = new ApplicationGroup();
        applicationGroup.setId(9999L);
        applicationGroup.setName("newGroup");
        applicationUser = new ApplicationUser();
        applicationUser.setAdmin(true);
        applicationUser.setId(9999L);
        applicationUser.setEmail(UUID.randomUUID() + "@gmail.com");
        applicationUser.setName("New user");
        applicationUser.setPassword("Password");
        groupRepository.save(applicationGroup);
        userRepository.save(applicationUser);
        userGroup = new UserGroup();
        userGroupKey = new UserGroupKey(applicationUser.getId(), applicationGroup.getId());
        userGroup.setId(userGroupKey);
        userGroup.setHost(true);
        userGroup.setUser(applicationUser);
        userGroup.setGroups(applicationGroup);
        userGroupRepository.save(userGroup);
    }

    @Test
    public void deleteById_deleteUserGroupByExistingId_expectedFalse() {
        Optional<UserGroup> expected = userGroupRepository.findById(userGroupKey);

        userGroupRepository.deleteById(userGroupKey);
        Optional<UserGroup> result = userGroupRepository.findById(userGroupKey);

        assertNotEquals(expected, result);
    }

    @Test
    public void deleteById_deleteUserGroupByNotExistingId_expectedTrue() {
        UserGroup expected = userGroupRepository.findById(userGroupKey).get();

        userGroupRepository.deleteById(new UserGroupKey());
        UserGroup result = userGroupRepository.findById(userGroupKey).get();

        assertEquals(expected.getGroup().getId(), result.getGroup().getId());
        assertEquals(expected.getUser().getId(), result.getUser().getId());
    }

    @Test
    public void findUsersByGroupId_findingUserOfGroup_expectedTwo() {
        ApplicationUser applicationUser1 = new ApplicationUser();
        applicationUser1.setAdmin(true);
        applicationUser1.setId(99998L);
        applicationUser1.setEmail(UUID.randomUUID() + "@gmail.com");
        applicationUser1.setName("New user");
        applicationUser1.setPassword("Password");
        userRepository.save(applicationUser1);
        ApplicationUser applicationUser2 = new ApplicationUser();
        applicationUser2.setAdmin(true);
        applicationUser2.setId(99997L);
        applicationUser2.setEmail(UUID.randomUUID() + "@gmail.com");
        applicationUser2.setName("New user");
        applicationUser2.setPassword("Password");
        userRepository.save(applicationUser2);
        UserGroupKey userGroupKey2 = new UserGroupKey(applicationUser2.getId(), applicationGroup.getId());
        UserGroup userGroup2 = new UserGroup();
        userGroup2.setId(userGroupKey2);
        userGroup2.setHost(false);
        userGroup2.setGroups(applicationGroup);
        userGroup2.setUser(applicationUser2);
        userGroupRepository.save(userGroup2);

        int expected = 2;
        int result = userGroupRepository.findUsersByGroupId(applicationGroup.getId()).size();

        assertEquals(result, expected);
    }

}
