package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.*;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.GroupMapper;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.GroupService;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.server.ResponseStatusException;

import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping(path = UserEndpoint.BASE_PATH)
public class UserEndpoint {

    static final String BASE_PATH = "/api/v1/users";

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserService userService;

    @Autowired
    public UserEndpoint(GroupService groupService, GroupMapper groupMapper, UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PermitAll
    public List<UserListDto> search(UserSearchDto searchParams) {
        LOGGER.info("GET " + BASE_PATH);
        LOGGER.debug("Request Params: {}", searchParams);
        return userService.search(searchParams);
    }

    @PostMapping
    @PermitAll
    @ResponseStatus(HttpStatus.CREATED)
    public void createUser(@Valid @RequestBody UserCreateDto userCreateDto) {
        LOGGER.info("POST /api/v1/user body: {}", userCreateDto);
        try {
            userService.register(userCreateDto);
        } catch (ConstraintViolationException e) {
            logClientError(HttpStatus.BAD_REQUEST, "Failed to create user since the email is already in use", e);
            HttpStatus status = HttpStatus.BAD_REQUEST;
            throw new ResponseStatusException(status, e.getMessage(), e);
        } catch (Exception e) {
            logClientError(HttpStatus.BAD_REQUEST, "Failed to create user", e);
            HttpStatus status = HttpStatus.BAD_REQUEST;
            throw new ResponseStatusException(status, e.getMessage(), e);

        }
    }

    private void logClientError(HttpStatus status, String message, Exception e) {
        LOGGER.warn("{} {}: {}: {}", status.value(), message, e.getClass().getSimpleName(), e.getMessage());
    }

    @PostMapping("/forgot-password")
    @PermitAll
    @ResponseStatus(HttpStatus.CREATED)
    public void forgotPassword(@RequestBody UserEmailDto email) {
        LOGGER.info("POST /api/v1/user body: {}", email);
        try {
            userService.forgotPassword(email.getEmail());
        } catch (NotFoundException e) {
            HttpStatus status = HttpStatus.NOT_FOUND;
            logClientError(status, "Failed to send email", e);
            throw new ResponseStatusException(status, e.getMessage(), e);
        }
    }

    @PutMapping("/reset-password")
    @PermitAll
    @ResponseStatus(HttpStatus.CREATED)
    public void resetPassword(@RequestBody ResetPasswordDto resetPasswordDTO) {
        LOGGER.info("PUT /api/v1/user body: {}", resetPasswordDTO);
        try {
            userService.resetPassword(resetPasswordDTO);
        } catch (NotFoundException e) {
            HttpStatus status = HttpStatus.NOT_FOUND;
            logClientError(status, "Failed to reset password", e);
            throw new ResponseStatusException(status, e.getMessage(), e);
        }
    }
}
