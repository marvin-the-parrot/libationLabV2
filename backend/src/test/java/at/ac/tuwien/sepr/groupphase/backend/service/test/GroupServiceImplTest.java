package at.ac.tuwien.sepr.groupphase.backend.service.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationGroup;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
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
	
	private ApplicationGroup applicationGroup;
	
	private ApplicationUser applicationUser;
	
	@BeforeEach
	public void setUp() {
		applicationGroup = new ApplicationGroup();
		applicationGroup.setId(9999L);
		applicationGroup.setName("newGroup");
		applicationUser = new ApplicationUser();
		applicationUser.setAdmin(true);
		applicationUser.setId(9999L);
		applicationUser.setEmail("newOne@user.at");
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
		
		groupServiceImpl.deleteGroup(-60l, applicationUser.getId());
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
	
}
