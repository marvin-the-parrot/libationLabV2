package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import java.lang.invoke.MethodHandles;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.IngredientListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MenuCocktailsDetailViewDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.RecommendedMenuesDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.security.SecurityRolesEnum;
import at.ac.tuwien.sepr.groupphase.backend.service.MenuService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MenuCocktailsDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.MenuServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.transaction.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Menu endpoint.
 */
@RestController
@RequestMapping(path = MenuEndpoint.BASE_PATH)
public class MenuEndpoint {

    static final String BASE_PATH = "/api/v1/menu";
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final String ROLE_USER = SecurityRolesEnum.Roles.ROLE_USER;
    private final MenuService menuService;

    @Autowired
    public MenuEndpoint(MenuService menuService) {
        this.menuService = menuService;
    }

    /**
     * Creating a new group entry.
     *
     * @param toCreate the group entry to create
     * @return the created group entry
     * @throws ValidationException if the data is not valid
     * @throws ConflictException   if the data conflicts with existing data
     */
    @Secured(ROLE_USER)
    @PostMapping()
    @Operation(security = @SecurityRequirement(name = "apiKey"))
    @ResponseStatus(HttpStatus.CREATED)
    public MenuCocktailsDto create(@RequestBody MenuCocktailsDto toCreate) throws ValidationException, ConflictException {
        LOGGER.info("POST " + BASE_PATH + "/{}", toCreate);
        LOGGER.debug("Body of request:\n{}", toCreate);
        return menuService.create(toCreate);
    }

    @Secured(ROLE_USER)
    @GetMapping(value = "/{id}")
    @Transactional
    @Operation(summary = "Get cocktails menu of specific group", security = @SecurityRequirement(name = "apiKey"))
    public MenuCocktailsDto getMenu(@PathVariable Long id) {
        LOGGER.info("GET " + BASE_PATH + "/{}", id);
        return menuService.findMenuOfGroup(id);
    }

    @Secured(ROLE_USER)
    @PutMapping()
    @Transactional
    @Operation(summary = "Update mixable cocktails of groups when changing ingredients")
    public void updateMixableCocktails(@RequestBody IngredientListDto[] userIngredients) {
        LOGGER.info("POST " + BASE_PATH + "/updateMixableCocktails");
        menuService.updateMixableCocktails();
    }

    @Secured(ROLE_USER)
    @Transactional
    @GetMapping(value = "/{id}/recommendation")
    public RecommendedMenuesDto getAutomatedMenu(@PathVariable Long id,
                                                 @RequestParam(name = "numberOfCocktails", required = false, defaultValue = "5") Integer numberOfCocktails) {
        LOGGER.info("GET " + BASE_PATH + "recommendation/{}", id);
        try {
            return menuService.createRecommendation(id, numberOfCocktails, 1);
        } catch (IllegalArgumentException e) {
            LOGGER.error("Error while creating recommendation", e);
            HttpStatus status = HttpStatus.BAD_REQUEST;
            throw new ResponseStatusException(status, e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.error("Error while creating recommendation", e);
            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            throw new ResponseStatusException(status, e.getMessage(), e);
        }

    }

    @Secured(ROLE_USER)
    @Transactional
    @GetMapping(value = "/{groupId}/detail")
    public MenuCocktailsDetailViewDto getMenuDetail(@PathVariable Long groupId) {
        LOGGER.info("GET " + BASE_PATH + "/{groupId}/detail");

        try {
            return menuService.findMenuDetailOfGroup(groupId);
        } catch (NotFoundException e) {
            LOGGER.error("Error while creating recommendation", e);
            HttpStatus status = HttpStatus.NOT_FOUND;
            throw new ResponseStatusException(status, e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.error("Error while creating recommendation", e);
            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            throw new ResponseStatusException(status, e.getMessage(), e);
        }
    }


}
