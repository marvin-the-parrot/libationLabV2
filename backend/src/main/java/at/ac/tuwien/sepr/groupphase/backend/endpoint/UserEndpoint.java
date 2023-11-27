package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PasswordResetDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.GroupMapper;
import at.ac.tuwien.sepr.groupphase.backend.service.GroupService;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.stream.Stream;

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

    @PutMapping("/{id}/reset")
    @PermitAll
    @ResponseStatus(HttpStatus.CREATED)
    public void resetPassword(@Valid @RequestBody PasswordResetDto passwordResetDto, @PathVariable String id) {
        LOGGER.info("POST /api/v1/user body: {}", passwordResetDto);
        userService.resetPassword(passwordResetDto);
    }

    private void logClientError(HttpStatus status, String message, Exception e) {
        LOGGER.warn("{} {}: {}: {}", status.value(), message, e.getClass().getSimpleName(), e.getMessage());
    }
}
