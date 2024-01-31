package at.ac.tuwien.sepr.groupphase.backend.endpoint.test;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ResetPasswordDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserEmailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserLocalStorageDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserLoginDto;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles({"test", "generateData"})
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

    @Test
    @Rollback
    @Transactional
    public void forgotPassword_requestResetMailCorrectMail_expectSuccessfulMailSent() throws Exception {
        UserEmailDto userEmailDto = new UserEmailDto();
        userEmailDto.setEmail("user1@email.com");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userEmailDto)))
            .andExpect(status().isCreated()).andReturn();
    }

    @Test
    @Rollback
    @Transactional
    public void forgotPassword_requestResetPasswordIncorrectMail_expectIsCreatedBecauseOfSecurityReasons() throws Exception {
        UserEmailDto userEmailDto = new UserEmailDto();
        userEmailDto.setEmail("user1@wrongDomain.com");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userEmailDto)))
            .andExpect(status().isCreated()).andReturn();
    }

    @Test
    @Rollback
    @Transactional
    public void resetPassword_resetPasswordWithIncorrectToken_expectError() throws Exception {
        ResetPasswordDto resetPasswordDto = new ResetPasswordDto();
        resetPasswordDto.setToken("WRONG_TOKEN");
        resetPasswordDto.setPassword("newPassword");
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/users/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resetPasswordDto)))
            .andExpect(status().isNotFound()).andReturn();
    }

    @Test
    @WithMockUser(roles = {"USER"}, username = "user1@email.com")
    @Transactional
    @Rollback
    public void deleteUser_deleteUser_expectSuccessfulDeletionAndLoginNotPossible() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/users/delete"))
            .andExpect(status().isOk()).andReturn();

        UserLoginDto loginDto = new UserLoginDto();
        loginDto.setEmail("user1@email.com");
        loginDto.setPassword("password");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/authentication")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
            .andExpect(status().isUnauthorized()).andReturn();
    }

    @Test
    @WithMockUser(roles = {"USER"}, username = "user1@email.com")
    @Transactional
    @Rollback
    public void deleteUser_deleteUserTwice_NotFoundException() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/users/delete"))
            .andExpect(status().isOk()).andReturn();

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/users/delete"))
            .andExpect(status().isNotFound()).andReturn();
    }

    @Test
    @WithMockUser(roles = {"USER"}, username = "user1@email.com")
    @Transactional
    @Rollback
    public void getUser_getUser_expectSuccessfulGet() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users/user"))
            .andExpect(status().isOk()).andReturn();

        UserLocalStorageDto user = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserLocalStorageDto.class);
        assertAll(
            () -> assertEquals("User1", user.getName()),
            () -> assertEquals("user1@email.com", user.getEmail()),
            () -> assertEquals(1L, user.getId())
        );
    }

    @Test
    @WithMockUser(roles = {"USER"}, username = "user1@wrongDomain.com")
    @Transactional
    @Rollback
    public void getUser_getUsernonExistingUser_expectError() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users/user"))
            .andExpect(status().isNotFound()).andReturn();
    }

    @Test
    @WithMockUser(username = "user1@email.com")
    public void search_searchUserExistingGroupWithExistingData_expectSuccessfulSearchAndListOfUserListDto() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users?name=u&groupId=1"))
            .andExpect(status().isOk()).andReturn();

        List<UserListDto> users = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<List<UserListDto>>() {
        });
        assertAll(
            () -> assertEquals(5, users.size()),
            () -> assertEquals("User2", users.get(0).getName()),
            () -> assertEquals("User5", users.get(1).getName()),
            () -> assertEquals("User6", users.get(2).getName()),
            () -> assertEquals("User7", users.get(3).getName()),
            () -> assertEquals("User8", users.get(4).getName())
        );
    }

    @Test
    @WithMockUser(username = "user1@email.com")
    public void search_searchUserExistingGroupWithNotExistingDataa_expectedIsOkAndEmptyListBecauseNoErrorShouldBeThrown() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users?name=x&groupId=1"))
            .andExpect(status().isOk()).andReturn();

        List<UserListDto> users = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<List<UserListDto>>() {
        });

        assertEquals(0, users.size());
    }

    @Test
    @WithMockUser(username = "user1@email.com")
    public void search_searchUserExistingGroupWithBadRequest_expectedIsBadRequest() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users?name=x&groupId=WRONG_GROUP_ID"))
            .andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    @WithMockUser(username = "user1@email.com")
    public void search_searchUserCreatingGroupWithValidData_expectSuccessfulSearchAndListOfUserListDto() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users?name=u"))
            .andExpect(status().isOk()).andReturn();

        List<UserListDto> users = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<List<UserListDto>>() {
        });
        assertAll(
            () -> assertEquals(5, users.size()),
            () -> assertEquals("User2", users.get(0).getName()),
            () -> assertEquals("User3", users.get(1).getName()),
            () -> assertEquals("User4", users.get(2).getName()),
            () -> assertEquals("User5", users.get(3).getName()),
            () -> assertEquals("User6", users.get(4).getName())
        );
    }

}
