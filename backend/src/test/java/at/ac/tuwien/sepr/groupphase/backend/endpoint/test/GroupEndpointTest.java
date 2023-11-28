package at.ac.tuwien.sepr.groupphase.backend.endpoint.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationGroup;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;

@SpringBootTest
@EnableWebMvc
@WebAppConfiguration
@TestInstance(Lifecycle.PER_CLASS)
public class GroupEndpointTest {

	  @Autowired
	  private WebApplicationContext webAppContext;
	  private MockMvc mockMvc;

	  @Autowired
	  private ObjectMapper objectMapper;
	  
	  @Autowired
	  private GroupRepository groupRepository;
	  
	  @Autowired
	  private UserRepository userRepository;
	  
	  private ApplicationGroup applicationGroup;
		
	  private ApplicationUser applicationUser;

	  @BeforeAll
	  public void setup() {
	    this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
		applicationGroup = new ApplicationGroup();
		applicationGroup.setId(999L);
		applicationGroup.setName("newGroup");
		applicationUser = new ApplicationUser();
		applicationUser.setAdmin(true);
		applicationUser.setId(999L);
		applicationUser.setEmail(UUID.randomUUID().toString()+"@gmail.com");
		applicationUser.setName("New user");
		applicationUser.setPassword("Password");
		groupRepository.save(applicationGroup);
		userRepository.save(applicationUser);		
	  }

	  @Test
	  @WithMockUser(roles = {"ADMIN"}) 
	  public void deleteGroup_deleteGroupByExistingIdAndFromHost_expectedFalse() throws Exception {
		int expected = groupRepository.findAll().size();
	    mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/groups/{id}/{userId}", applicationGroup.getId(), applicationUser.getId())).andExpect(status().isNoContent());
		int result = groupRepository.findAll().size();
	    assertNotEquals(expected, result);
	  }

	  @Test
	  @WithMockUser(roles = {"ADMIN"}) 
	  public void deleteGroup_deleteGroupByNotExistingIdAndFromHost_expectedTrue() throws Exception {
		int expected = groupRepository.findAll().size();
	    mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/groups/{id}/{userId}", -60L, applicationUser.getId())).andExpect(status().isNoContent());
		int result = groupRepository.findAll().size();
	    assertEquals(expected, result);
	  }
	  
	  @Test
	  @WithMockUser(roles = {"ADMIN"}) 
	  public void deleteGroup_deleteGroupByExistingIdAndFromNotHost_expectedTrue() throws Exception {
		applicationUser.setAdmin(false);
		userRepository.save(applicationUser);
		int expected = groupRepository.findAll().size();
	    mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/groups/{id}/{userId}", applicationGroup.getId(), applicationUser.getId())).andExpect(status().isBadRequest());
		int result = groupRepository.findAll().size();
		assertEquals(expected, result);
	  }
	  
}
