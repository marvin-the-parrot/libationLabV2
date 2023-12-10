package at.ac.tuwien.sepr.groupphase.backend.service.test;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.GroupCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserListDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
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

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
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
        assertEquals(groupCreateDto.getName(), createdGroupDto.getName());
        assertEquals(groupCreateDto.getHost(), createdGroupDto.getHost());
        assertArrayEquals(groupCreateDto.getMembers(), createdGroupDto.getMembers());

        int numberOfGroupsAfter = groupRepository.findAll().size();
        assertEquals(numberOfGroupsBefore + 1, numberOfGroupsAfter);

        ApplicationGroup newGroup = groupRepository.findById(createdGroupDto.getId()).orElse(null);
        assertNotNull(newGroup);
        assertEquals(groupCreateDto.getName(), newGroup.getName());
    }

    @Test
    public void create_createGroupByNonExistentUser_expectedException() {
        GroupCreateDto groupCreateDto = createBasicGroup();
        // create nonexistent host and add him to the members
        UserListDto host = new UserListDto();
        host.setId(9999L);
        host.setName("Nonexistent user");
        groupCreateDto.setHost(host);
        var members = groupCreateDto.getMembers();
        members[0].setId(9999L);
        members[0].setName("Nonexistent user");
        groupCreateDto.setMembers(members);

        ConflictException conflictException = assertThrows(ConflictException.class, () -> groupService.create(groupCreateDto));
        assertEquals("Validation of group for create failed. Conflicts: Group host does not exist, Group member Nonexistent user does not exist.",
            conflictException.getMessage());
    }

    @Test
    public void create_createGroupByExistingUserWithNonExistentMember_expectedException() {
        GroupCreateDto groupCreateDto = createBasicGroup();
        // create nonexistent member
        UserListDto[] members = groupCreateDto.getMembers();
        members[1].setId(9999L);
        members[1].setName("Nonexistent user");
        groupCreateDto.setMembers(members);

        ConflictException conflictException = assertThrows(ConflictException.class, () -> groupService.create(groupCreateDto));
        assertEquals("Validation of group for create failed. Conflicts: Group member Nonexistent user does not exist.", conflictException.getMessage());
    }

    @Test
    public void create_createGroupByExistingUserWhichIsNotInMembers_expectedException() {
        GroupCreateDto groupCreateDto = createBasicGroup();
        // remove host from members
        UserListDto[] members = groupCreateDto.getMembers();
        members = Arrays.copyOfRange(members, 1, members.length);
        groupCreateDto.setMembers(members);

        ValidationException validationException = assertThrows(ValidationException.class, () -> groupService.create(groupCreateDto));
        assertEquals("Validation of group for create failed. Failed validations: Group host must be in group members.", validationException.getMessage());
    }

    @Test
    public void create_createGroupByExistingUserWithDuplicateMembers_expectedException() {
        GroupCreateDto groupCreateDto = createBasicGroup();
        // add duplicate member
        UserListDto[] members = groupCreateDto.getMembers();
        members = Arrays.copyOf(members, members.length + 1);
        members[members.length - 1] = members[1];
        groupCreateDto.setMembers(members);

        ValidationException validationException = assertThrows(ValidationException.class, () -> groupService.create(groupCreateDto));
        assertEquals("Validation of group for create failed. Failed validations: Group member must not be added twice.", validationException.getMessage());
    }

    @Test
    public void create_createGroupWithoutName_expectedException() {
        GroupCreateDto groupCreateDto = createBasicGroup();
        groupCreateDto.setName(null);

        ValidationException validationException = assertThrows(ValidationException.class, () -> groupService.create(groupCreateDto));
        assertEquals("Validation of group for create failed. Failed validations: Group name must not be empty.", validationException.getMessage());

        groupCreateDto.setName("");
        validationException = assertThrows(ValidationException.class, () -> groupService.create(groupCreateDto));
        assertEquals("Validation of group for create failed. Failed validations: Group name must not be empty.", validationException.getMessage());
    }

    @Test
    public void create_createGroupWithTooLongName_expectedException() {
        GroupCreateDto groupCreateDto = createBasicGroup();
        groupCreateDto.setName("a".repeat(256));

        ValidationException validationException = assertThrows(ValidationException.class, () -> groupService.create(groupCreateDto));
        assertEquals("Validation of group for create failed. Failed validations: Group name must not be longer than 255 characters.",
            validationException.getMessage());
    }

    @Test
    public void create_createGroupWithoutHost_expectedException() {
        GroupCreateDto groupCreateDto = createBasicGroup();
        groupCreateDto.setHost(null);

        ValidationException validationException = assertThrows(ValidationException.class, () -> groupService.create(groupCreateDto));
        assertEquals("Validation of group for create failed. Failed validations: Group host must not be null, Group host must be in group members.",
            validationException.getMessage());
    }

    @Test
    public void create_createGroupWithoutMembers_expectedException() {
        GroupCreateDto groupCreateDto = createBasicGroup();
        groupCreateDto.setMembers(null);

        ValidationException validationException = assertThrows(ValidationException.class, () -> groupService.create(groupCreateDto));
        assertEquals("Validation of group for create failed. Failed validations: Group members must not be null.", validationException.getMessage());
    }

    @Test
    public void create_createGroupWithNullMember_expectedException() {
        GroupCreateDto groupCreateDto = createBasicGroup();
        groupCreateDto.getMembers()[1] = null;

        ValidationException validationException = assertThrows(ValidationException.class, () -> groupService.create(groupCreateDto));
        assertEquals("Validation of group for create failed. Failed validations: Group member must not be null.", validationException.getMessage());
    }

    @Test
    public void create_createGroupWithoutMemberId_expectedException() {
        GroupCreateDto groupCreateDto = createBasicGroup();
        groupCreateDto.getMembers()[1].setId(null);

        ValidationException validationException = assertThrows(ValidationException.class, () -> groupService.create(groupCreateDto));
        assertEquals("Validation of group for create failed. Failed validations: Group member id must not be null.", validationException.getMessage());
    }

    // update group (edit)
    @Test
    public void update_updateExistingGroupFromHost_expectedTrue() {
        GroupCreateDto groupCreateDto = createBasicGroup();
        groupCreateDto.setId(1L);
        GroupCreateDto updatedGroupDto = assertDoesNotThrow(() -> groupService.update(groupCreateDto, "user1@email.com"));
        assertNotNull(updatedGroupDto);
        assertEquals(groupCreateDto.getName(), updatedGroupDto.getName());
        assertEquals(groupCreateDto.getHost(), updatedGroupDto.getHost());

        // edit checks if some members already exist in the group (so the order of the members might be shuffled)
        assertThat(updatedGroupDto.getMembers()).extracting("id", "name")
            .containsExactlyInAnyOrder(tuple(1L, "User1"), tuple(2L, "User2"), tuple(3L, "User3"), tuple(4L, "User4"), tuple(5L, "User5"));
    }

    @Test
    public void update_updateGroupWithId0_expectedException() {
        GroupCreateDto groupCreateDto = createBasicGroup();
        groupCreateDto.setId(0L);

        ValidationException validationException = assertThrows(ValidationException.class, () -> groupService.update(groupCreateDto, "user1@email.com"));
        assertEquals("Validation of group for update failed. Failed validations: Group id must not be 0.", validationException.getMessage());
    }

    @Test
    public void update_updateNonExistentGroup_expectedException() {
        GroupCreateDto groupCreateDto = createBasicGroup();
        groupCreateDto.setId(9999L);

        ValidationException validationException = assertThrows(ValidationException.class, () -> groupService.update(groupCreateDto, "user1@email.com"));
        assertEquals("This action is not allowed. Failed validations: You are not the host of this group.", validationException.getMessage());
    }

    @Test
    public void update_updateGroupByNonexistentUser_expectedException() {
        GroupCreateDto groupCreateDto = createBasicGroup();
        groupCreateDto.setId(1L);

        NotFoundException notFoundException = assertThrows(NotFoundException.class, () -> groupService.update(groupCreateDto, "nonexisten@email.com"));
        assertEquals("Could not find current user", notFoundException.getMessage());
    }

    @Test
    public void update_updateGroupRemovesMembersFromGroup_expectedTrue() {
        // expecting DB to contain the following group:
        var groupMembers = userGroupRepository.findAllByApplicationGroup(groupRepository.findById(1L).orElse(null));
        assertEquals(3, groupMembers.size());
        assertEquals(1L, groupMembers.get(0).getUser().getId());
        assertEquals(3L, groupMembers.get(1).getUser().getId());
        assertEquals(4L, groupMembers.get(2).getUser().getId());

        // update group:
        GroupCreateDto groupCreateDto = createBasicGroup();
        groupCreateDto.setId(1L);
        groupCreateDto.setMembers(Arrays.copyOfRange(groupCreateDto.getMembers(), 0, 2)); // this should remove user 3 and 4 from the group (and add user 2)
        GroupCreateDto updatedGroupDto = assertDoesNotThrow(() -> groupService.update(groupCreateDto, "user1@email.com"));

        assertNotNull(updatedGroupDto);
        assertEquals(groupCreateDto.getName(), updatedGroupDto.getName());
        assertEquals(groupCreateDto.getHost(), updatedGroupDto.getHost());
        assertThat(updatedGroupDto.getMembers()).extracting("id", "name").containsExactlyInAnyOrder(tuple(1L, "User1"), tuple(2L, "User2"));
    }

    // make member host
    @Test
    public void makeMemberHost_makeMemberHostByExistingIdAndFromHost_expectedTrue() {

        assertDoesNotThrow(() -> groupServiceImpl.makeMemberHost(1L, 3L, "user1@email.com"));

        GroupCreateDto group = groupServiceImpl.applicationGroupToGroupCreateDto(groupRepository.findById(1L).orElse(null));
        assertEquals(group.getHost().getId(), 3L);
    }

    @Test
    public void makeMemberHost_makeNonexistentMemberHost_expectedException() {
        NotFoundException notFoundException = assertThrows(NotFoundException.class, () -> groupServiceImpl.makeMemberHost(1L, 9999L, "user1@email.con"));
    }

    @Test
    public void makeMemberHost_makeMemberHostByExistingIdAndNotFromHost_expectedException() {

        ValidationException validationStatusException =
            assertThrows(ValidationException.class, () -> groupServiceImpl.makeMemberHost(1L, 3L, "user3@email.com"));

        assertEquals("This action is not allowed. Failed validations: You are not the host of this group.", validationStatusException.getMessage());
    }

    @Test
    public void makeMemberHost_makeMemberHostOfNonexistentGroup_expectedException() {
        NotFoundException notFoundException = assertThrows(NotFoundException.class, () -> groupServiceImpl.makeMemberHost(9999L, 3L, "user1@email.com"));
    }

    @Test
    public void makeMemberHost_makeUserThatIsNoMemberOfGroupHost_expectedException() {
        NotFoundException notFoundException = assertThrows(NotFoundException.class, () -> groupServiceImpl.makeMemberHost(1L, 2L, "user1@email.com"));
    }

    @Test
    public void makeMemberHost_makeUserHostByNonexistentUser_expectedException() {
        NotFoundException notFoundException = assertThrows(NotFoundException.class, () -> groupServiceImpl.makeMemberHost(1L, 3L, "nonexistent@mail.cmom"));
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
        NotFoundException notFoundException = assertThrows(NotFoundException.class,
            () -> groupServiceImpl.deleteGroup(-60L, applicationUser.getEmail()));

        assertEquals("Could not find group", notFoundException.getMessage());
    }

    @Test
    public void deleteGroup_deleteGroupByExistingIdAndNotFromHost_expectedException() {
        generateTestData();
        applicationUser.setAdmin(false);
        userRepository.save(applicationUser);

        ValidationException validationStatusException = assertThrows(ValidationException.class,
            () -> groupServiceImpl.deleteGroup(applicationGroup.getId(), applicationUser.getEmail()));

        assertEquals("This action is not allowed. Failed validations: You are not the host of this group.", validationStatusException.getMessage());
    }

    // delete member
    @Test
    public void deleteMember_deleteGroupByExistingIdAndFromHost_expectedFalse() throws ValidationException {generateTestData();
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

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
            () -> groupServiceImpl.deleteMember(applicationGroup.getId(), applicationUser.getId(), "wrong@email.com"));

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
