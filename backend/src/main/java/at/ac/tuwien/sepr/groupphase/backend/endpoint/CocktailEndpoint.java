package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import java.lang.invoke.MethodHandles;
import java.util.List;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CocktailDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CocktailSerachDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.IngredientListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PreferenceListDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.security.SecurityRolesEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CocktailListDto;
import at.ac.tuwien.sepr.groupphase.backend.service.CocktailService;
import org.springframework.web.server.ResponseStatusException;

/**
 * Cocktail endpoint controller.
 */
@RestController
@RequestMapping(path = CocktailEndpoint.BASE_PATH)
public class CocktailEndpoint {

    static final String BASE_PATH = "/api/v1/cocktails";
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final String ROLE_USER = SecurityRolesEnum.Roles.ROLE_USER;
    private final CocktailService cocktailService;

    @Autowired
    public CocktailEndpoint(CocktailService cocktailService) {
        this.cocktailService = cocktailService;
    }

    @Secured(ROLE_USER)
    @GetMapping("/{id}")
    public CocktailDetailDto getCocktailById(@PathVariable Long id) {
        LOGGER.info("GET " + BASE_PATH + "/{}", id);
        try {
            return cocktailService.getCocktailById(id);
        } catch (NotFoundException e) {
            HttpStatus status = HttpStatus.NOT_FOUND;
            logClientError(status, "Failed to get cocktail", e);
            throw new ResponseStatusException(status, e.getMessage(), e);
        }
    }

    @Secured(ROLE_USER)
    @GetMapping
    public List<CocktailListDto> searchCocktails(CocktailSerachDto searchParameters) {
        LOGGER.info("GET " + BASE_PATH + "/searchCocktails/{}", searchParameters);
        LOGGER.debug("Request Params:\n{}", searchParameters);

        return cocktailService.searchCocktails(searchParameters);
    }


    @Secured(ROLE_USER)
    @GetMapping("/cocktail-ingredients-auto/{ingredientsName}")
    @ResponseStatus(HttpStatus.OK)
    public List<IngredientListDto> searchIngredientsAuto(@PathVariable String ingredientsName) {
        LOGGER.info("GET " + BASE_PATH + "/cocktail-ingredients-auto/{}", ingredientsName);

        return cocktailService.searchAutoIngredients(ingredientsName);
    }

    @GetMapping("/cocktail-preferences-auto/{preferenceName}")
    @Secured(ROLE_USER)
    @ResponseStatus(HttpStatus.OK)
    public List<PreferenceListDto> searchPreferencesAuto(@PathVariable String preferenceName) {
        LOGGER.info("GET " + BASE_PATH + "/cocktail-preference-auto/{}", preferenceName);

        return cocktailService.searchAutoPreferences(preferenceName);
    }

    private void logClientError(HttpStatus status, String message, Exception e) {
        LOGGER.warn("{} {}: {}: {}", status.value(), message, e.getClass().getSimpleName(), e.getMessage());
    }

}
