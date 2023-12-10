package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import java.lang.invoke.MethodHandles;
import java.util.List;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CocktailOverviewDto;
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

import com.fasterxml.jackson.core.JsonProcessingException;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CocktailListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.IngredientListDto;
import at.ac.tuwien.sepr.groupphase.backend.service.CocktailIngredientService;
import at.ac.tuwien.sepr.groupphase.backend.service.IngredientService;

/**
 * Cocktail endpoint controller.
 */
@RestController
@RequestMapping(path = CocktailEndpoint.BASE_PATH)
public class CocktailEndpoint {

    private static final Logger LOGGER =
        LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    static final String BASE_PATH = "/api/v1/cocktails";
    private final CocktailIngredientService cocktailService;

    @Autowired
    public CocktailEndpoint(CocktailIngredientService cocktailService) {
        this.cocktailService = cocktailService;
    }

    @Secured("ROLE_USER")
    @GetMapping("searchCocktails/{cocktailName}/{ingredientsName}")
    @ResponseStatus(HttpStatus.OK)
    public List<CocktailListDto> searchCoctails(@PathVariable String cocktailName, @PathVariable String ingredientsName) throws JsonProcessingException {
        LOGGER.info("GET " + BASE_PATH + "searchCocktails/{}", cocktailName, ingredientsName);
        return cocktailService.searchCocktailByCocktailNameAndIngredientName(cocktailName, ingredientsName);
    }

    @Secured("ROLE_USER")
    @GetMapping("searchCocktails/{cocktailName}")
    @ResponseStatus(HttpStatus.OK)
    public List<CocktailListDto> searchCoctails(@PathVariable String cocktailName) throws JsonProcessingException {
        LOGGER.info("GET " + BASE_PATH + "searchCocktails/{}", cocktailName);
        return cocktailService.searchCocktailByCocktailNameAndIngredientName(cocktailName, null);
    }

    @Secured("ROLE_USER")
    @GetMapping("searchCocktails/ingredient/{ingredientsName}")
    @ResponseStatus(HttpStatus.OK)
    public List<CocktailListDto> searchCoctailsByIngredient(@PathVariable String ingredientsName) throws JsonProcessingException {
        LOGGER.info("GET " + BASE_PATH + "searchCocktails/ingredient/{}", ingredientsName);
        return cocktailService.searchCocktailByCocktailNameAndIngredientName(null, ingredientsName);
    }

}
