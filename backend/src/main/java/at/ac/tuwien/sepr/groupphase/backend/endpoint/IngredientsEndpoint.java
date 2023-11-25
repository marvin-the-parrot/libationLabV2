package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredients;
import at.ac.tuwien.sepr.groupphase.backend.service.IngredientsService;

import java.lang.invoke.MethodHandles;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Ingredients endpoint controller.
 */
@RestController
@RequestMapping(path = IngredientsEndpoint.BASE_PATH)
public class IngredientsEndpoint {

    private static final Logger LOGGER =
        LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    static final String BASE_PATH = "/api/v1/ingredients";
    @Autowired
    private IngredientsService ingredientsService;

    @Secured("ROLE_USER")
    @RequestMapping(value = "searchIngredients/{ingredientsName}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public Optional<Ingredients> searchIngredients(@PathVariable String ingredientsName) {
        LOGGER.info("GET " + BASE_PATH + "searchIngredients/{}", ingredientsName);
        return ingredientsService.searchIngredients(ingredientsName);
    }

}
