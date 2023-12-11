package at.ac.tuwien.sepr.groupphase.backend.endpoint.test;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationGroup;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.UserGroup;
import at.ac.tuwien.sepr.groupphase.backend.entity.UserGroupKey;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserGroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("generateData")
@SpringBootTest
public class UserEndpointTest {

    @Autowired
    private WebApplicationContext webAppContext;
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
    }

    @Test
    public void loginUser_loginUserWithCorrectCredentials_expectSuccess() throws Exception {

        UserLoginDto loginDto = new UserLoginDto();
        loginDto.setEmail("user1@email.com");
        loginDto.setPassword("password");

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/authentication")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
            .andExpect(status().isOk()).andReturn();
    }


    @Test
    public void loginUser_loginUserWithWrongCredentials_expectUnauhtorized() throws Exception {

        UserLoginDto loginDto = new UserLoginDto();
        loginDto.setEmail("user1@email.com");
        loginDto.setPassword("WRONG_PASSWORD");

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/authentication")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
            .andExpect(status().isUnauthorized()).andReturn();
    }

    @Test
    @Rollback
    @Transactional
    public void registerUser_registerUserWithCorrectCredentials_expectSuccessfulLogin() throws Exception {

        UserCreateDto user = new UserCreateDto();
        user.setEmail("test@mail.com");
        user.setPassword("password");
        user.setName("testName");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
            .andExpect(status().isCreated()).andReturn();

        UserLoginDto loginDto = new UserLoginDto();
        loginDto.setEmail(user.getEmail());
        loginDto.setPassword(user.getPassword());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/authentication")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
            .andExpect(status().isOk()).andReturn();
    }

    @Test
    @Rollback
    @Transactional
    public void registerUser_registerUserWithShortPassword_expectError() throws Exception {

        UserCreateDto user = new UserCreateDto();
        user.setEmail("test@mail.com");
        user.setPassword("pwd");
        user.setName("testName");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
            .andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    @Rollback
    @Transactional
    public void registerUser_registerUserWithNonFittingMail_expectError() throws Exception {

        UserCreateDto user = new UserCreateDto();
        user.setEmail("test.com");
        user.setPassword("password");
        user.setName("testName");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
            .andExpect(status().isBadRequest()).andReturn();
    }






}
