package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CocktailFeedbackDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.FeedbackCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.repository.FeedbackRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.SecurityRolesEnum;
import at.ac.tuwien.sepr.groupphase.backend.service.FeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
    @PostMapping("/create")
    @Operation(summary = "Save a feedback")
    public void create(@RequestBody FeedbackCreateDto feedbackToCreate) {
        LOGGER.info("POST " + BASE_PATH + "/create: {}", feedbackToCreate);

        if (feedbackToCreate == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Recommendation must not be null");
        }

        try {
            feedbackService.create(feedbackToCreate);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Secured(ROLE_USER)
    @PutMapping("/update")
    @Operation(summary = "Update a feedback")
    public void update(@RequestBody CocktailFeedbackDto feedbackToUpdate) {
        LOGGER.info("PUT " + BASE_PATH + "/update: {}", feedbackToUpdate);

        if (feedbackToUpdate == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Recommendation must not be null");
        }

        try {
            feedbackService.update(feedbackToUpdate);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
