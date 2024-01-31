package at.ac.tuwien.sepr.groupphase.backend.endpoint.test;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CocktailDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.GroupCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.GroupOverviewDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserListDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationGroup;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.UserGroup;
import at.ac.tuwien.sepr.groupphase.backend.entity.UserGroupKey;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserGroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.GroupService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles({"test", "generateData"})
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class GroupEndpointTest {

    @Autowired
    private WebApplicationContext webAppContext;
    private MockMvc mockMvc;

    @Autowired
    private UserGroupRepository userGroupRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupService groupService;

    private ApplicationGroup applicationGroup;

    private ApplicationUser applicationUser;

    private ApplicationUser applicationUserMember;

    private UserGroup userGroup;

    private UserGroupKey userGroupKey;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setupMockMvc() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
    }


    @Test
    @WithMockUser(username = "user1@email.com")
    public void create_createGroupByValidUser_expectedSuccess() throws Exception {
        // create new group
        GroupCreateDto groupCreateDto = new GroupCreateDto();
        groupCreateDto.setName("newTestGroup");
        ApplicationUser currentUser = userRepository.findByEmail("user1@email.com");
        UserListDto currentUserDto = new UserListDto();
        currentUserDto.setId(currentUser.getId());
        currentUserDto.setName(currentUser.getName());
        groupCreateDto.setHost(currentUserDto);
        groupCreateDto.setMembers(new UserListDto[] {currentUserDto});
        // send request
        MvcResult mvcResult = mockMvc
            .perform(MockMvcRequestBuilders.post("/api/v1/groups").contentType("application/json").content(objectMapper.writeValueAsString(groupCreateDto)))
            .andExpect(status().isCreated()).andReturn();
        String contentResult = mvcResult.getResponse().getContentAsString();
        // check response
        GroupCreateDto result = objectMapper.readValue(contentResult, GroupCreateDto.class);
        assertEquals(groupCreateDto.getName(), result.getName());
        assertEquals(groupCreateDto.getHost().getId(), result.getHost().getId());
        assertEquals(groupCreateDto.getHost().getName(), result.getHost().getName());
        // check database
        Optional<ApplicationGroup> group = groupRepository.findById(result.getId());
        assertTrue(group.isPresent());
        assertEquals(groupCreateDto.getName(), group.get().getName());
    }

    @Test
    @WithMockUser(username = "user1@email.com")
    public void update_updateGroupByValidUser_expectedTrue() throws Exception {
        // create new group
        GroupCreateDto groupCreateDto = new GroupCreateDto();
        groupCreateDto.setName("newTestGroup");
        ApplicationUser currentUser = userRepository.findByEmail("user1@email.com");
        UserListDto currentUserDto = new UserListDto();
        currentUserDto.setId(currentUser.getId());
        currentUserDto.setName(currentUser.getName());
        groupCreateDto.setHost(currentUserDto);
        groupCreateDto.setMembers(new UserListDto[] {currentUserDto});
        // send request
        MvcResult mvcResult = mockMvc
            .perform(MockMvcRequestBuilders.post("/api/v1/groups").contentType("application/json").content(objectMapper.writeValueAsString(groupCreateDto)))
            .andExpect(status().isCreated()).andReturn();
        String contentResult = mvcResult.getResponse().getContentAsString();
        // check response
        GroupCreateDto result = objectMapper.readValue(contentResult, GroupCreateDto.class);
        assertEquals(groupCreateDto.getName(), result.getName());
        assertEquals(groupCreateDto.getHost().getId(), result.getHost().getId());
        assertEquals(groupCreateDto.getHost().getName(), result.getHost().getName());
        // check database
        Optional<ApplicationGroup> group = groupRepository.findById(result.getId());
        assertTrue(group.isPresent());
        assertEquals(groupCreateDto.getName(), group.get().getName());

        // update group
        groupCreateDto.setName("newTestGroup2");
        groupCreateDto.setId(result.getId());
        // send request
        mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/groups/{id}", result.getId()).contentType("application/json")
            .content(objectMapper.writeValueAsString(groupCreateDto))).andExpect(status().isOk()).andReturn();
        contentResult = mvcResult.getResponse().getContentAsString();
        // check response
        result = objectMapper.readValue(contentResult, GroupCreateDto.class);
        assertEquals(groupCreateDto.getName(), result.getName());
        assertEquals(groupCreateDto.getHost().getId(), result.getHost().getId());
        assertEquals(groupCreateDto.getHost().getName(), result.getHost().getName());
        // check database
        group = groupRepository.findById(result.getId());
        assertTrue(group.isPresent());
        assertEquals(groupCreateDto.getName(), group.get().getName());
    }


    @Test
    @WithMockUser(username = "user1@email.com")
    public void find_getRequestFromMemberOfGroup_expectedSuccess() throws Exception {
        // create new group
        GroupCreateDto groupCreateDto = new GroupCreateDto();
        groupCreateDto.setName("TestGroup1");
        ApplicationUser currentUser = userRepository.findByEmail("user1@email.com");
        UserListDto currentUserDto = new UserListDto();
        currentUserDto.setId(currentUser.getId());
        currentUserDto.setName(currentUser.getName());
        groupCreateDto.setHost(currentUserDto);
        groupCreateDto.setMembers(new UserListDto[] {currentUserDto});
        GroupCreateDto createdGroup = groupService.create(groupCreateDto);
        assertNotNull(groupRepository.findById(createdGroup.getId()));
        // send request
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/groups/{id}", createdGroup.getId())).andExpect(status().isOk()).andReturn();
        String contentResult = mvcResult.getResponse().getContentAsString();
        // check response
        GroupCreateDto result = objectMapper.readValue(contentResult, GroupCreateDto.class);
        assertEquals("TestGroup1", result.getName());
        assertEquals(currentUserDto.getId(), result.getHost().getId());
    }


    @Test
    @WithMockUser(username = "user1@email.com")
    public void makeMemberHost_makeMemberHostByHost_expectedSuccess() throws Exception {
        // create new group with 2 members
        GroupCreateDto groupCreateDto = new GroupCreateDto();
        groupCreateDto.setName("TestGroup2");
        // current user (host)
        ApplicationUser currentUser = userRepository.findByEmail("user1@email.com");
        UserListDto currentUserDto = new UserListDto();
        currentUserDto.setId(currentUser.getId());
        currentUserDto.setName(currentUser.getName());
        // other user (member)
        ApplicationUser otherUser = userRepository.findByEmail("user2@email.com");
        UserListDto otherUserDto = new UserListDto();
        otherUserDto.setId(otherUser.getId());
        otherUserDto.setName(otherUser.getName());
        // create group
        groupCreateDto.setHost(currentUserDto);
        groupCreateDto.setMembers(new UserListDto[] {currentUserDto, otherUserDto});
        GroupCreateDto createdGroup = groupService.create(groupCreateDto);

        ApplicationGroup group = groupRepository.findById(createdGroup.getId()).orElse(null);
        assertNotNull(group);
        // make other user join group
        UserGroup userGroup =
            UserGroup.UserGroupBuilder.userGroup().withUserGroupKey(new UserGroupKey(otherUser.getId(), group.getId())).withUser(otherUser).withGroup(group)
                .withIsHost(false).build();
        userGroupRepository.save(userGroup);

        // check database
        List<UserGroup> usersBefore = userGroupRepository.findAllByApplicationGroup(group);
        assertEquals(2, usersBefore.size());

        UserGroup user1 = usersBefore.get(0);
        UserGroup user2 = usersBefore.get(1);

        if (user1.getUser().getId() == currentUserDto.getId()) {
            assertTrue(user1.isHost());
            assertFalse(user2.isHost());
        } else {
            assertTrue(user2.isHost());
            assertFalse(user1.isHost());
        }

        // send request
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/groups/{groupId}/{userId}", createdGroup.getId(), otherUserDto.getId())).andExpect(status().isOk())
            .andReturn();
        // check database
        List<UserGroup> usersAfterwards = userGroupRepository.findAllByApplicationGroup(group);
        assertNotNull(usersAfterwards);
        assertEquals(2, usersAfterwards.size());

        user1 = usersAfterwards.get(0);
        user2 = usersAfterwards.get(1);

        if (user1.getUser().getId() == currentUserDto.getId()) {
            assertFalse(user1.isHost());
            assertTrue(user2.isHost());
        } else {
            assertFalse(user2.isHost());
            assertTrue(user1.isHost());
        }
    }


    @Test
    @WithMockUser(roles = {"USER"})
    public void deleteGroup_deleteGroupByExistingIdAndFromHost_expectedFalse() throws Exception {
        setup();
        prepareUserGroupAndMember();
        int expected = groupRepository.findAll().size();
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/groups/{id}", userGroup.getId())).andExpect(status().isBadRequest());
        int result = groupRepository.findAll().size();
        assertEquals(expected, result);
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void deleteGroup_deleteGroupByNotExistingIdAndFromHost_expectedTrue() throws Exception {
        setup();
        int expected = groupRepository.findAll().size();
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/groups/{id}", -60L, applicationUser.getId())).andExpect(status().isNotFound());
        int result = groupRepository.findAll().size();
        assertEquals(expected, result);
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void deleteGroup_deleteGroupByExistingIdAndFromNotHost_expectedTrue() throws Exception {
        setup();
        applicationUser.setAdmin(false);
        userRepository.save(applicationUser);
        int expected = groupRepository.findAll().size();
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/groups/{id}", applicationGroup.getId())).andExpect(status().isNotFound());
        int result = groupRepository.findAll().size();
        assertEquals(expected, result);
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void deleteMember_deleteGroupByNotExistingIdAndFromHost_expectedTrue() throws Exception {
        setup();
        prepareUserGroupAndMember();
        Optional<UserGroup> expected = userGroupRepository.findById(userGroupKey);
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/groups/{groupId}/{userId}", -60L, applicationUser.getId(), applicationUserMember.getId()))
            .andExpect(status().isNotFound());
        Optional<UserGroup> result = userGroupRepository.findById(userGroupKey);

        assertEquals(expected.get().getGroup().getId(), result.get().getGroup().getId());
        assertEquals(expected.get().getUser().getId(), result.get().getUser().getId());
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void deleteMember_deleteGroupByExistingIdAndFromNotHost_expectedTrue() throws Exception {
        setup();
        prepareUserGroupAndMember();
        applicationUser.setAdmin(false);
        userRepository.save(applicationUser);
        List<UserGroup> expected = userGroupRepository.findAllByApplicationGroup(userGroup.getGroup());
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/groups/{userId}", applicationGroup.getId(), applicationUser.getId()))
            .andExpect(status().isNotFound());
        List<UserGroup> result = userGroupRepository.findAllByApplicationGroup(userGroup.getGroup());
        assertEquals(expected.size(), result.size());
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void searchForMember_searchingMembersOfGroupByGroupId_expected0() throws Exception {
        setup();
        this.prepareUserGroupAndMember();

        int expected = 0;
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/groups/searchGroupMember/{groupId}", applicationGroup.getId() + 9999))
            .andExpect(status().isOk()).andReturn();
        String contentResult = mvcResult.getResponse().getContentAsString();
        int result = objectMapper.readValue(contentResult, new TypeReference<List<UserListDto>>() {
        }).size();

        assertEquals(expected, result);
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void getMixableCocktails_getMixableCocktailsByGroupId_expected0() throws Exception {
        setup();
        this.prepareUserGroupAndMember();

        int expected = 0;
        MvcResult mvcResult =
            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/groups/{groupId}/mixables", applicationGroup.getId())).andExpect(status().isOk()).andReturn();
        String contentResult = mvcResult.getResponse().getContentAsString();
        int result = objectMapper.readValue(contentResult, new TypeReference<List<CocktailDetailDto>>() {
        }).size();

        assertEquals(expected, result);
    }

    @Test
    @WithMockUser(username = "user1@email.com")
    public void findGroupsByUser_findGroupsByUser_expected2() throws Exception {
        MvcResult mvcResult =
            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/groups")).andExpect(status().isOk()).andReturn();
        String contentResult = mvcResult.getResponse().getContentAsString();
        List<GroupOverviewDto> result = objectMapper.readValue(contentResult, new TypeReference<List<GroupOverviewDto>>() {
        });

        assertAll(
            () -> assertEquals("Group1", result.get(0).getName()),
            () -> assertEquals("Group2", result.get(1).getName())
        );
    }


    private void setup() {
        applicationGroup = new ApplicationGroup();
        applicationGroup.setId(999L);
        applicationGroup.setName("newGroup");
        applicationUser = new ApplicationUser();
        applicationUser.setAdmin(true);
        applicationUser.setId(999L);
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



}
