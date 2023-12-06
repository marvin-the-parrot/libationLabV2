package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import java.lang.invoke.MethodHandles;
import java.util.List;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.IngredientDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.IngredientMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredient;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import at.ac.tuwien.sepr.groupphase.backend.service.IngredientService;
import jakarta.annotation.security.PermitAll;
import org.springframework.web.server.ResponseStatusException;

/**
 * Ingredients endpoint controller.
 */
@RestController
@RequestMapping(path = IngredientEndpoint.BASE_PATH)
public class IngredientEndpoint {

    private static final Logger LOGGER =
        LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    static final String BASE_PATH = "/api/v1/ingredients";
    private final IngredientService ingredientService;
    private final IngredientMapper ingredientMapper;

    @Autowired
    public IngredientEndpoint(IngredientService ingredientService, IngredientMapper ingredientMapper) {
        this.ingredientService = ingredientService;
        this.ingredientMapper = ingredientMapper;
    }

    @PermitAll
    @RequestMapping(value = "searchIngredients/{ingredientsName}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public List<Ingredient> searchIngredients(@PathVariable String ingredientsName) {
        //TODO: don't permit all and no RequestMapping instead GetMapping
        LOGGER.info("GET " + BASE_PATH + "searchIngredients/{}", ingredientsName);
        return ingredientService.searchIngredients(ingredientsName);
    }

    @Secured("ROLE_USER")
    @GetMapping
    public List<IngredientDto> getAllGroupIngredients(@Valid Long groupId) {
        LOGGER.info("GET " + BASE_PATH + "getAllGroupIngredients/{}", groupId);
        try {
            return ingredientMapper.ingredientToIngredientDto(ingredientService.getAllGroupIngredients(groupId));
        } catch (NotFoundException e) {
            HttpStatus status = HttpStatus.NOT_FOUND;
            logClientError(status, "Group not found", e);
            throw new ResponseStatusException(status, e.getMessage(), e);
        }
    }

    private void logClientError(HttpStatus status, String message, Exception e) {
        LOGGER.warn("{} {}: {}: {}", status.value(), message,
            e.getClass().getSimpleName(), e.getMessage());
    }
}
