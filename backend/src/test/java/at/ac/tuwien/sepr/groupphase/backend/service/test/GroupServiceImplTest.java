package at.ac.tuwien.sepr.groupphase.backend.service.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;

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
		applicationGroup = new ApplicationGroup();
		applicationGroup.setId(9999L);
		applicationGroup.setName("newGroup");
		applicationUser = new ApplicationUser();
		applicationUser.setAdmin(true);
		applicationUser.setId(9999L);
		applicationUser.setEmail(UUID.randomUUID() +"@gmail.com");
		applicationUser.setName("New user");
		applicationUser.setPassword("Password");
		groupRepository.save(applicationGroup);
		userRepository.save(applicationUser);
	}

	@Test
	public void deleteGroup_deleteGroupByExistingIdAndFromHost_expectedFalse() {
		int expected = groupRepository.findAll().size();
		groupServiceImpl.deleteGroup(applicationGroup.getId(), applicationUser.getId());
		int result = groupRepository.findAll().size();

		assertNotEquals(expected, result);
	}

	@Test
	public void deleteGroup_deleteGroupByNotExistingIdAndFromHost_expectedTrue() {
		int expected = groupRepository.findAll().size();

		groupServiceImpl.deleteGroup(-60L, applicationUser.getId());
		int result = groupRepository.findAll().size();

		assertEquals(expected, result);
	}

	@Test
	public void deleteGroup_deleteGroupByExistingIdAndNotFromHost_expectedException() {
		applicationUser.setAdmin(false);
		userRepository.save(applicationUser);

		ResponseStatusException responseStatusException = assertThrows(ResponseStatusException.class,
                () -> groupServiceImpl.deleteGroup(applicationGroup.getId(), applicationUser.getId()));

        assertEquals("400 BAD_REQUEST", responseStatusException.getMessage());
	}

	@Test
	public void deleteMember_deleteGroupByExistingIdAndFromHost_expectedFalse() {
		prepareUserGroupAndMember();
		Optional<UserGroup> expected = userGroupRepository.findById(userGroupKey);

		groupServiceImpl.deleteMember(applicationGroup.getId(), applicationUser.getId(), applicationUserMember.getId());
		Optional<UserGroup> result = userGroupRepository.findById(userGroupKey);

		assertNotEquals(expected, result);
	}

	@Test
	public void deleteMember_deleteGroupByNotExistingIdAndFromHost_expectedTrue() {
		prepareUserGroupAndMember();
		Optional<UserGroup> expected = userGroupRepository.findById(userGroupKey);

		groupServiceImpl.deleteMember(applicationGroup.getId(), applicationUser.getId(), -60L);
		Optional<UserGroup> result = userGroupRepository.findById(userGroupKey);

		assertEquals(expected.get().getGroups().getId(), result.get().getGroups().getId());
		assertEquals(expected.get().getUser().getId(), result.get().getUser().getId());
	}

	@Test
	public void deleteMember_deleteGroupByExistingIdAndNotFromHost_expectedException() {
		prepareUserGroupAndMember();
		applicationUser.setAdmin(false);
		userRepository.save(applicationUser);

		ResponseStatusException responseStatusException = assertThrows(ResponseStatusException.class,
                () -> groupServiceImpl.deleteMember(applicationGroup.getId(), applicationUser.getId(), applicationUserMember.getId()));

        assertEquals("400 BAD_REQUEST", responseStatusException.getMessage());
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
		applicationUserMember.setEmail(UUID.randomUUID() +"@gmail.com");
		applicationUserMember.setName("New user member");
		applicationUserMember.setPassword("Password Member");
		userRepository.save(applicationUserMember);
	}

}
