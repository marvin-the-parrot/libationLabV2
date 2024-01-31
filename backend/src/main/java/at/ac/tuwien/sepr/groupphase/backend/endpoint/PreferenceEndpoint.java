package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PreferenceListDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.security.SecurityRolesEnum;
import at.ac.tuwien.sepr.groupphase.backend.service.PreferenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;

import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.List;

/**
 * Preference endpoint controller.
 */
@RestController
@RequestMapping(path = PreferenceEndpoint.BASE_PATH)
public class PreferenceEndpoint {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    static final String BASE_PATH = "/api/v1/preferences";
    private static final String ROLE_USER = SecurityRolesEnum.Roles.ROLE_USER;
    private final PreferenceService preferenceService;

    @Autowired
    public PreferenceEndpoint(PreferenceService preferenceService) {
        this.preferenceService = preferenceService;
    }

    @GetMapping("/user-preference-auto/{preferenceName}")
    @Secured(ROLE_USER)
    public List<PreferenceListDto> searchAutocomplete(@PathVariable String preferenceName) {
        LOGGER.info("GET " + BASE_PATH + "/user-preference-auto/{}", preferenceName);

        try {
            return preferenceService.searchUserPreferences(preferenceName);
        } catch (NotFoundException e) {
            HttpStatus status = HttpStatus.NOT_FOUND;
            logClientError(status, "Failed to search preferences", e);
            throw new ResponseStatusException(status, e.getMessage(), e);
        }
    }

    @GetMapping("/user-preferences")
    @Secured(ROLE_USER)
    public List<PreferenceListDto> getUserPreferences() {
        LOGGER.info("GET " + BASE_PATH + "/user-preferences");
        try {
            return preferenceService.getUserPreferences();
        } catch (NotFoundException e) {
            HttpStatus status = HttpStatus.NOT_FOUND;
            logClientError(status, "Failed to search preferences", e);
            throw new ResponseStatusException(status, e.getMessage(), e);
        }
    }

    @PostMapping("/user-preferences")
    @Secured(ROLE_USER)
    public List<PreferenceListDto> addUserPreferences(@RequestBody PreferenceListDto[] preferences) {
        LOGGER.info("POST " + BASE_PATH + "/user-preferences/{}", Arrays.toString(preferences));

        try {
            return preferenceService.addPreferencesToUser(preferences);
        } catch (NotFoundException e) {
            HttpStatus status = HttpStatus.NOT_FOUND;
            logClientError(status, "Failed to search preferences", e);
            throw new ResponseStatusException(status, e.getMessage(), e);
        } catch (ConflictException e) {
            HttpStatus status = HttpStatus.CONFLICT;
            logClientError(status, "Failed to search preferences", e);
            throw new ResponseStatusException(status, e.getMessage(), e);
        }
    }


    private void logClientError(HttpStatus status, String message, Exception e) {
        LOGGER.warn("{} {}: {}: {}", status.value(), message,
            e.getClass().getSimpleName(), e.getMessage());
    }



}
