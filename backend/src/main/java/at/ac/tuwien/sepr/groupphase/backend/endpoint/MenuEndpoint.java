package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import java.lang.invoke.MethodHandles;

import at.ac.tuwien.sepr.groupphase.backend.security.SecurityRolesEnum;
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

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MenuCocktailsDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.MenuServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.transaction.Transactional;

/**
 * Menu endpoint.
 */
@RestController
@RequestMapping(path = MenuEndpoint.BASE_PATH)
public class MenuEndpoint {

    static final String BASE_PATH = "/api/v1/menu";
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final String ROLE_USER = SecurityRolesEnum.Roles.ROLE_USER;
    private final MenuServiceImpl menuServiceImpl;

    @Autowired
    public MenuEndpoint(MenuServiceImpl menuServiceImpl) {
        this.menuServiceImpl = menuServiceImpl;
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
        return menuServiceImpl.create(toCreate);
    }

    @Secured(ROLE_USER)
    @GetMapping(value = "/{id}")
    @Transactional
    @Operation(summary = "Get cocktails menu of specific group", security = @SecurityRequirement(name = "apiKey"))
    public MenuCocktailsDto getMenu(@PathVariable Long id) {
        LOGGER.info("GET " + BASE_PATH + "/{}", id);
        return menuServiceImpl.findMenuOfGroup(id);
    }
}
