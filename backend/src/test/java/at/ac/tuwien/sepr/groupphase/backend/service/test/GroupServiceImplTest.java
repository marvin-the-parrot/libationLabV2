package at.ac.tuwien.sepr.groupphase.backend.service.test;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.GroupCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserListDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationGroup;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.UserGroup;
import at.ac.tuwien.sepr.groupphase.backend.entity.UserGroupKey;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserGroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.GroupService;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.GroupServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles({"test", "generateData"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
// indicates that the underlying Spring ApplicationContext has been dirtied during the execution of a test (that is, the test modified or corrupted it in some manner) and should be closed. Makes the tests very slow; necessary because some tests modify the database.
public class GroupServiceImplTest {

    @Autowired
    private GroupServiceImpl groupServiceImpl;

    @Autowired
    private GroupService groupService;

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
    }

    // create group
    @Test
    public void create_createGroupByExistingUser_expectedTrue() {
        int numberOfGroupsBefore = groupRepository.findAll().size();
        assertNull(groupRepository.findById((long) numberOfGroupsBefore + 1).orElse(null));

        GroupCreateDto groupCreateDto = createBasicGroup();
        GroupCreateDto createdGroupDto = assertDoesNotThrow(() -> groupService.create(groupCreateDto));
        assertNotNull(createdGroupDto);

        int numberOfGroupsAfter = groupRepository.findAll().size();
        assertEquals(numberOfGroupsBefore + 1, numberOfGroupsAfter);

        ApplicationGroup newGroup = groupRepository.findById(createdGroupDto.getId()).orElse(null);
        assertNotNull(newGroup);
        assertEquals(groupCreateDto.getName(), newGroup.getName());
    }


    // delete group
    @Test
    public void deleteGroup_deleteGroupByExistingIdAndFromHost_expectedFalse() throws ValidationException {
        generateTestData();
        prepareUserGroupAndMember();

        int expected = groupRepository.findAll().size();
        groupServiceImpl.deleteGroup(applicationGroup.getId(), applicationUser.getEmail());
        int result = groupRepository.findAll().size();

        assertNotEquals(expected, result);
    }

    @Test
    public void deleteGroup_deleteGroupByNotExistingIdAndFromHost_expectedTrue() throws ValidationException {
        generateTestData();
        NotFoundException notFoundException = assertThrows(NotFoundException.class, () -> groupServiceImpl.deleteGroup(-60L, applicationUser.getEmail()));

        assertEquals("Could not find group", notFoundException.getMessage());
    }

    @Test
    public void deleteGroup_deleteGroupByExistingIdAndNotFromHost_expectedException() {
        generateTestData();
        applicationUser.setAdmin(false);
        userRepository.save(applicationUser);

        ValidationException validationStatusException =
            assertThrows(ValidationException.class, () -> groupServiceImpl.deleteGroup(applicationGroup.getId(), applicationUser.getEmail()));

        assertEquals("This action is not allowed. Failed validations: You are not the host of this group.", validationStatusException.getMessage());
    }

    // delete member
    @Test
    public void deleteMember_deleteGroupByExistingIdAndFromHost_expectedFalse() throws ValidationException {
        generateTestData();
        prepareUserGroupAndMember();
        Optional<UserGroup> expected = userGroupRepository.findById(userGroupKey);

        groupServiceImpl.deleteMember(applicationGroup.getId(), applicationUser.getId(), applicationUserMember.getEmail());
        Optional<UserGroup> result = userGroupRepository.findById(userGroupKey);

        assertNotEquals(expected, result);
    }

    @Test
    public void deleteMember_deleteGroupByNotExistingIdAndFromHost_expectedTrue() throws ValidationException {
        generateTestData();
        prepareUserGroupAndMember();

        NotFoundException notFoundException =
            assertThrows(NotFoundException.class, () -> groupServiceImpl.deleteMember(applicationGroup.getId(), applicationUser.getId(), "wrong@email.com"));

        assertEquals("Could not find current user", notFoundException.getMessage());
    }

    @Test
    public void deleteMember_deleteGroupByExistingIdAndNotFromHost_expectedException() {
        generateTestData();
        prepareUserGroupAndMember();
        applicationUser.setAdmin(false);
        userRepository.save(applicationUser);
        //userGroupKey = new UserGroupKey(applicationUserMember.getId(), applicationGroup.getId());
        //userGroup.setId(userGroupKey);
        userGroup.setHost(false);
        userGroupRepository.save(userGroup);

        ValidationException validationStatusException = assertThrows(ValidationException.class,
            () -> groupServiceImpl.deleteMember(applicationGroup.getId(), applicationUserMember.getId(), applicationUser.getEmail()));

        assertEquals("You are not allowed to remove this user from the group. Failed validations: .", validationStatusException.getMessage());
    }

    // search for group members
    @Test
    public void searchForMember_searchingMembersOfGroupByGroupId_expected2() {
        generateTestData();
        this.prepareUserGroupAndMember();

        int expected = 2;
        int result = userGroupRepository.findUsersByGroupId(applicationGroup.getId()).size();

        assertEquals(result, expected);
    }

    /**
     * Create a ApplicationGroup and a ApplicationUser for testing
     * todo: to generate test data, the generateData profile should be used
     */
    private void generateTestData() {
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


    /**
     * Creates a basic (valid) group with a host and 5 members
     *
     * @return GroupCreateDto
     */
    private GroupCreateDto createBasicGroup() {
        // create host
        UserListDto host = new UserListDto();
        host.setId(1L);
        host.setName("User1");
        // create members
        UserListDto[] members = new UserListDto[5];
        for (int i = 0; i < members.length; i++) {
            UserListDto member = new UserListDto();
            member.setId((long) i + 1);
            member.setName("User" + (i + 1));
            members[i] = member;
        }
        // create group
        GroupCreateDto groupCreateDto = new GroupCreateDto();
        groupCreateDto.setName("newGroup");
        groupCreateDto.setHost(host);
        groupCreateDto.setMembers(members);

        return groupCreateDto;
    }

}
