package at.ac.tuwien.sepr.groupphase.backend.endpoint.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserListDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationGroup;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.UserGroup;
import at.ac.tuwien.sepr.groupphase.backend.entity.UserGroupKey;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserGroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;

@ActiveProfiles("generateData")
@SpringBootTest
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

    private ApplicationGroup applicationGroup;

    private ApplicationUser applicationUser;

    private ApplicationUser applicationUserMember;

    private UserGroup userGroup;

    private UserGroupKey userGroupKey;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
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

	  @Test
	  @WithMockUser(roles = {"USER"})
	  public void deleteGroup_deleteGroupByExistingIdAndFromHost_expectedFalse() throws Exception {
        prepareUserGroupAndMember();
		int expected = groupRepository.findAll().size();
	    mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/groups/{id}", userGroup.getId())).andExpect(status().isBadRequest());
		int result = groupRepository.findAll().size();
	    assertEquals(expected, result);
	  }

	  @Test
	  @WithMockUser(roles = {"USER"})
	  public void deleteGroup_deleteGroupByNotExistingIdAndFromHost_expectedTrue() throws Exception {
		int expected = groupRepository.findAll().size();
	    mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/groups/{id}", -60L, applicationUser.getId())).andExpect(status().isNotFound());
		int result = groupRepository.findAll().size();
	    assertEquals(expected, result);
	  }

	  @Test
	  @WithMockUser(roles = {"USER"})
	  public void deleteGroup_deleteGroupByExistingIdAndFromNotHost_expectedTrue() throws Exception {
		applicationUser.setAdmin(false);
		userRepository.save(applicationUser);
		int expected = groupRepository.findAll().size();
	    mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/groups/{id}", applicationGroup.getId())).andExpect(status().isNotFound());
		int result = groupRepository.findAll().size();
		assertEquals(expected, result);
	  }

	  @Test
	  @WithMockUser(roles = {"USER"})
	  public void deleteMember_deleteGroupByExistingIdAndFromHost_expectedFalse() throws Exception {
		prepareUserGroupAndMember();
		Optional<UserGroup> expected = userGroupRepository.findById(userGroupKey);

		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/groups/{groupId}/{userId}", applicationGroup.getId(), applicationUserMember.getId())).andExpect(status().isNotFound());
		Optional<UserGroup> result = userGroupRepository.findById(userGroupKey);

	    assertNotEquals(expected, result);
	  }

	  @Test
	  @WithMockUser(roles = {"USER"})
	  public void deleteMember_deleteGroupByNotExistingIdAndFromHost_expectedTrue() throws Exception {
		prepareUserGroupAndMember();
		Optional<UserGroup> expected = userGroupRepository.findById(userGroupKey);
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/groups/{groupId}/{userId}", -60L, applicationUser.getId(), applicationUserMember.getId())).andExpect(status().isNotFound());
		Optional<UserGroup> result = userGroupRepository.findById(userGroupKey);

		assertEquals(expected.get().getGroup().getId(), result.get().getGroup().getId());
		assertEquals(expected.get().getUser().getId(), result.get().getUser().getId());
	  }

	  @Test
	  @WithMockUser(roles = {"USER"})
	  public void deleteMember_deleteGroupByExistingIdAndFromNotHost_expectedTrue() throws Exception {
		prepareUserGroupAndMember();
		applicationUser.setAdmin(false);
		userRepository.save(applicationUser);
		List<UserGroup> expected = userGroupRepository.findAllByApplicationGroup(userGroup.getGroup());
	    mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/groups/{userId}", applicationGroup.getId(), applicationUser.getId())).andExpect(status().isNotFound());
		List<UserGroup> result = userGroupRepository.findAllByApplicationGroup(userGroup.getGroup());
		assertEquals(expected.size(), result.size());
	  }

    @Test
    @WithMockUser(roles = {"USER"})
    public void searchForMember_searchingMembersOfGroupByGroupId_expected0() throws Exception {
        this.prepareUserGroupAndMember();

        int expected = 0;
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/groups/searchGroupMember/{groupId}", applicationGroup.getId()+9999)).andExpect(status().isOk()).andReturn();
        String contentResult = mvcResult.getResponse().getContentAsString();
        int result = objectMapper.readValue(contentResult, new TypeReference<List<UserListDto>>() {
        }).size();

        assertEquals(expected, result );
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
