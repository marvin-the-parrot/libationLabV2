package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.IngredientGroupDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.IngredientListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.IngredientSuggestionDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.security.SecurityRolesEnum;
import at.ac.tuwien.sepr.groupphase.backend.service.IngredientService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.List;

/**
 * Ingredients endpoint controller.
 */
@RestController
@RequestMapping(path = IngredientEndpoint.BASE_PATH)
public class  IngredientEndpoint {

    private static final Logger LOGGER =
        LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    static final String BASE_PATH = "/api/v1/ingredients";
    private static final String ROLE_USER = SecurityRolesEnum.Roles.ROLE_USER;
    private final IngredientService ingredientService;

    @Autowired
    public IngredientEndpoint(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    @Secured(ROLE_USER)
    @GetMapping("searchIngredients/{ingredientsName}")
    @ResponseStatus(HttpStatus.OK)
    public List<IngredientListDto> searchIngredients(@PathVariable String ingredientsName) throws JsonProcessingException {
        LOGGER.info("GET " + BASE_PATH + "/searchIngredients/{}", ingredientsName);
        return ingredientService.searchIngredients(ingredientsName);
    }

    @Secured(ROLE_USER)
    @GetMapping("/{groupId}")
    public List<IngredientGroupDto> getAllGroupIngredients(@PathVariable Long groupId) {
        LOGGER.info("GET " + BASE_PATH + "/getAllGroupIngredients/{}", groupId);
        try {
            return ingredientService.getAllGroupIngredients(groupId);
        } catch (NotFoundException e) {
            HttpStatus status = HttpStatus.NOT_FOUND;
            logClientError(status, "Group not found", e);
            throw new ResponseStatusException(status, e.getMessage(), e);
        }
    }

    @Secured(ROLE_USER)
    @GetMapping("/suggestions/{groupId}")
    public List<IngredientSuggestionDto> getIngredientSuggestions(@PathVariable Long groupId) throws ConflictException {
        LOGGER.info("GET " + BASE_PATH + "/getIngredientSuggestions/{}", groupId);
        try {
            return ingredientService.getIngredientSuggestions(groupId);
        } catch (NotFoundException e) {
            HttpStatus status = HttpStatus.NOT_FOUND;
            logClientError(status, e.getMessage(), e);
            throw new ResponseStatusException(status, e.getMessage(), e);
        }
    }


    @GetMapping("/user-ingredients-auto/{ingredientsName}")
    @Secured(ROLE_USER)
    public List<IngredientListDto> searchAutocomplete(@PathVariable String ingredientsName) {
        LOGGER.info("GET " + BASE_PATH + "/user-ingredients-auto/{}", ingredientsName);

        try {
            return ingredientService.searchUserIngredients(ingredientsName);
        } catch (NotFoundException e) {
            HttpStatus status = HttpStatus.NOT_FOUND;
            logClientError(status, "Failed to search ingredients", e);
            throw new ResponseStatusException(status, e.getMessage(), e);
        }
    }

    @GetMapping("/user-ingredients")
    @Secured(ROLE_USER)
    public List<IngredientListDto> getUserIngredients() {
        LOGGER.info("GET " + BASE_PATH + "/user-ingredients");
        try {
            return ingredientService.getUserIngredients();
        } catch (NotFoundException e) {
            HttpStatus status = HttpStatus.NOT_FOUND;
            logClientError(status, "Failed to search ingredients", e);
            throw new ResponseStatusException(status, e.getMessage(), e);
        }
    }

    @PostMapping("/user-ingredients")
    @Secured(ROLE_USER)
    public List<IngredientListDto> addUserIngredients(@RequestBody IngredientListDto[] ingredients) {
        LOGGER.info("POST " + BASE_PATH + "/user-ingredients/{}", Arrays.toString(ingredients));
        LOGGER.debug("Request Body:\n{}", Arrays.toString(ingredients));
        try {
            return ingredientService.addIngredientsToUser(ingredients);
        } catch (NotFoundException e) {
            HttpStatus status = HttpStatus.NOT_FOUND;
            logClientError(status, "Failed to search ingredients", e);
            throw new ResponseStatusException(status, e.getMessage(), e);
        } catch (ConflictException e) {
            HttpStatus status = HttpStatus.CONFLICT;
            logClientError(status, "Failed to search ingredients", e);
            throw new ResponseStatusException(status, e.getMessage(), e);
        }
    }

    private void logClientError(HttpStatus status, String message, Exception e) {
        LOGGER.warn("{} {}: {}: {}", status.value(), message,
            e.getClass().getSimpleName(), e.getMessage());
    }
}
