package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CocktailFeedbackDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.FeedbackCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.security.SecurityRolesEnum;
import at.ac.tuwien.sepr.groupphase.backend.service.FeedbackService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.InvalidEndpointRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.lang.invoke.MethodHandles;

@RestController
@RequestMapping(value = "/api/v1/feedback")
public class FeedbackEndpoint {

    static final String BASE_PATH = "/api/v1/feedback";
    private static final Logger LOGGER =
        LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final String ROLE_USER = SecurityRolesEnum.Roles.ROLE_USER;
    private final FeedbackService feedbackService;

    @Autowired
    public FeedbackEndpoint(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @Secured(ROLE_USER)
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/create")
    public void create(@Valid @RequestBody FeedbackCreateDto feedbackToCreate) {
        LOGGER.info("POST " + BASE_PATH + "/create: {}", feedbackToCreate);
        LOGGER.debug("Request Body:\n{}", feedbackToCreate);
        try {
            feedbackService.createFeedbackRelations(feedbackToCreate);
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (InvalidEndpointRequestException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Secured(ROLE_USER)
    @PutMapping("/update")
    public void update(@RequestBody CocktailFeedbackDto feedbackToUpdate) {
        LOGGER.info("PUT " + BASE_PATH + "/update: {}", feedbackToUpdate);
        LOGGER.debug("Request Body:\n{}", feedbackToUpdate);

        if (feedbackToUpdate == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Recommendation must not be null");
        }

        try {
            feedbackService.updateRatings(feedbackToUpdate);
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Secured(ROLE_USER)
    @DeleteMapping("/delete/{groupId}/{userId}")
    public void delete(@PathVariable Long groupId, @PathVariable Long userId) {
        LOGGER.info("DELETE " + BASE_PATH + "/{}/{}", groupId, userId);

        try {
            feedbackService.deleteFeedbackRelationsAtCocktailChange(groupId, userId);
        } catch (NotFoundException e) {
            HttpStatus status = HttpStatus.NOT_FOUND;
            logClientError(status, "Feedback to delete not found", e);
            throw new ResponseStatusException(status, e.getMessage(), e);
        }
    }

    private void logClientError(HttpStatus status, String message, Exception e) {
        LOGGER.warn("{} {}: {}: {}", status.value(), message,
            e.getClass().getSimpleName(), e.getMessage());
    }
}
