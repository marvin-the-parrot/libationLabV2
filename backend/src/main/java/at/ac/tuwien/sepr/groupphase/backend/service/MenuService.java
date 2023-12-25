package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MenuCocktailsDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;

public interface MenuService {
    /**
     * Find menu by group id.
     *
     * @param groupId the id of the group entry
     * @return the menu
     */
    MenuCocktailsDto findMenuOfGroup(Long groupId) throws NotFoundException;

    /**
     * Creates a new or update given menu with the data given in {@code toCreate}.
     *
     * @param toCreate the data of the menu to create
     * @return the created or updated menu
     */
    MenuCocktailsDto create(MenuCocktailsDto toCreate) throws ConflictException;
}
