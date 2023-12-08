package at.ac.tuwien.sepr.groupphase.backend.service.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
import at.ac.tuwien.sepr.groupphase.backend.service.impl.GroupServiceImpl;

@SpringBootTest
public class GroupServiceImplTest {

    @Autowired
    private GroupServiceImpl groupServiceImpl;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserGroupRepository userGroupRepository;

    private ApplicationGroup applicationGroup;

    private ApplicationUser applicationUser;

    private ApplicationUser applicationUserMember;

    private UserGroup userGroup;

    private UserGroupKey userGroupKey;

    @BeforeEach
    public void setUp() {
        userGroupRepository.deleteAll();
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
    }

    @Test
    public void searchForMember_searchingMembersOfGroupByGroupId_expected2() {
        this.prepareUserGroupAndMember();

        int expected = 2;
        int result = userGroupRepository.findUsersByGroupId(applicationGroup.getId()).size();

        assertEquals(result, expected);
    }

    private void prepareUserGroupAndMember() {
        userGroup = new UserGroup();
        userGroupKey = new UserGroupKey(applicationUser.getId(), applicationGroup.getId());
        userGroup.setId(userGroupKey);
        userGroup.setHost(true);
        userGroup.setUser(applicationUser);
        userGroup.setGroups(applicationGroup);
        userGroupRepository.save(userGroup);
        applicationUserMember = new ApplicationUser();
        applicationUserMember.setAdmin(false);
        applicationUserMember.setId(9999L);
        applicationUserMember.setEmail(UUID.randomUUID() + "@gmail.com");
        applicationUserMember.setName("New user member");
        applicationUserMember.setPassword("Password Member");
        userRepository.save(applicationUserMember);
        UserGroup userGroup2 = new UserGroup();
        UserGroupKey userGroupKey2 = new UserGroupKey(applicationUserMember.getId(), applicationGroup.getId());
        userGroup2.setId(userGroupKey2);
        userGroup2.setHost(true);
        userGroup2.setUser(applicationUser);
        userGroup2.setGroups(applicationGroup);
        userGroupRepository.save(userGroup2);
    }

}
