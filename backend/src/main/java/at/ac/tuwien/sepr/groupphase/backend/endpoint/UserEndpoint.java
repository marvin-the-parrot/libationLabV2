package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ResetPasswordDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserEmailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserLocalStorageDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserSearchExistingGroupDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.security.SecurityRolesEnum;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
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
    private static final String ROLE_USER = SecurityRolesEnum.Roles.ROLE_USER;
    private final UserService userService;

    @Autowired
    public UserEndpoint(UserService userService) {
        this.userService = userService;
    }

    @Secured(ROLE_USER)
    @GetMapping
    public List<UserListDto> search(@Valid UserSearchExistingGroupDto searchParams) {
        LOGGER.info("GET " + BASE_PATH);
        LOGGER.debug("Request Params: {}", searchParams);
        try {
            return userService.search(searchParams);
        } catch (Exception e) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            logClientError(status, "Failed to search users", e);
            throw new ResponseStatusException(status, e.getMessage(), e);
        }
    }

    @PostMapping
    @PermitAll
    @ResponseStatus(HttpStatus.CREATED)
    public void createUser(@Valid @RequestBody UserCreateDto userCreateDto) {
        LOGGER.info("POST " + BASE_PATH + "/{}", userCreateDto.getEmail());

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

    @PostMapping("/forgot-password")
    @PermitAll
    @ResponseStatus(HttpStatus.CREATED)
    public void forgotPassword(@RequestBody UserEmailDto email) {
        LOGGER.info("POST " + BASE_PATH + "/forgot-password/{}", email);
        LOGGER.debug("Request Body:\n{}", email);

        try {
            userService.forgotPassword(email.getEmail());
        } catch (NotFoundException e) {
            HttpStatus status = HttpStatus.NOT_FOUND;
            logClientError(status, "Failed to send email", e);
        }
    }

    @PutMapping("/reset-password")
    @PermitAll
    @ResponseStatus(HttpStatus.CREATED)
    public void resetPassword(@RequestBody ResetPasswordDto resetPasswordDto) {
        LOGGER.info("PUT " + BASE_PATH + "/reset-password/{}", resetPasswordDto.getToken());

        try {
            userService.resetPassword(resetPasswordDto);
        } catch (NotFoundException e) {
            HttpStatus status = HttpStatus.NOT_FOUND;
            logClientError(status, "Failed to reset password", e);
            throw new ResponseStatusException(status, e.getMessage(), e);
        } catch (ValidationException e) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            logClientError(status, "Failed to reset password", e);
            throw new ResponseStatusException(status, e.getMessage(), e);
        }
    }

    @GetMapping("/user")
    @PermitAll
    @ResponseStatus(HttpStatus.OK)
    public UserLocalStorageDto getUser() {
        LOGGER.info("GET " + BASE_PATH + "/user");

        try {
            return userService.getUserByEmail();
        } catch (NotFoundException e) {
            HttpStatus status = HttpStatus.NOT_FOUND;
            logClientError(status, String.format("Failed to get user with mail %s", SecurityContextHolder.getContext().getAuthentication().getName()), e);
            throw new ResponseStatusException(status, e.getMessage(), e);
        }
    }

    @DeleteMapping("/delete")
    @Secured(ROLE_USER)
    @ResponseStatus(HttpStatus.OK)
    public void deleteUser() {
        LOGGER.info("DELETE " + BASE_PATH + "/delete");
        try {
            userService.deleteUserByEmail();
        } catch (NotFoundException e) {
            HttpStatus status = HttpStatus.NOT_FOUND;
            logClientError(status, String.format("Failed to delete user with mail %s", SecurityContextHolder.getContext().getAuthentication().getName()), e);
            throw new ResponseStatusException(status, e.getMessage(), e);
        }
    }

    private void logClientError(HttpStatus status, String message, Exception e) {
        LOGGER.warn("{} {}: {}: {}", status.value(), message, e.getClass().getSimpleName(), e.getMessage());
    }
}
