package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MenuCocktailsDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MenuRecommendationDto;
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

    /**
     * Creates a new recommendation for the given group.
     *
     * @param groupId of the group for which the recommendation should be created
     * @param seed    the seed for the random generator to generate different recommendations
     * @param numberOfCocktails the number of cocktails to be recommended
     * @return the created recommendation
     */
    MenuRecommendationDto createRecommendation(Long groupId, Long seed, Integer numberOfCocktails);
}
