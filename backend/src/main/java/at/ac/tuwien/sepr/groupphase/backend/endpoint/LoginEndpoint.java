package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import jakarta.annotation.security.PermitAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.lang.invoke.MethodHandles;

import static at.ac.tuwien.sepr.groupphase.backend.endpoint.LoginEndpoint.BASE_PATH;

/**
 * Login endpoint.
 */
@RestController
@RequestMapping(value = BASE_PATH)
public class LoginEndpoint {

    static final String BASE_PATH = "/api/v1/authentication";
    private final UserService userService;
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public LoginEndpoint(UserService userService) {
        this.userService = userService;
    }

    @PermitAll
    @PostMapping
    public String login(@RequestBody UserLoginDto userLoginDto) {
        LOGGER.info("POST " + BASE_PATH + "/{}", userLoginDto);
        LOGGER.debug("Request Body:\n{}", userLoginDto);
        try {
            return userService.login(userLoginDto);
        } catch (BadCredentialsException | UsernameNotFoundException e) {
            logClientError(HttpStatus.UNAUTHORIZED, "Bad credentials", e);
            HttpStatus status = HttpStatus.UNAUTHORIZED;
            throw new ResponseStatusException(status, e.getMessage(), e);
        } catch (Exception e) {
            logClientError(HttpStatus.INTERNAL_SERVER_ERROR, "Error while logging in", e);
            throw new RuntimeException(e);
        }

    }

    private void logClientError(HttpStatus status, String message, Exception e) {
        LOGGER.warn("{} {}: {}: {}", status.value(), message, e.getClass().getSimpleName(), e.getMessage());
    }
}
