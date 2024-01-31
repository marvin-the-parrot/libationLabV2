package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MenuCocktailsDetailViewDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MenuCocktailsDetailViewHostDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MenuCocktailsDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.RecommendedMenuesDto;
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
     * Find menu by group id with ratings for each cocktail
     * to display for when you are host.
     *
     * @param groupId the id of the group entry
     * @return the menu
     */
    MenuCocktailsDetailViewHostDto getMenuWithRatings(Long groupId) throws NotFoundException;

    /**
     * Creates a new or update given menu with the data given in {@code toCreate}.
     *
     * @param toCreate the data of the menu to create
     * @return the created or updated menu
     */
    MenuCocktailsDto create(MenuCocktailsDto toCreate) throws NotFoundException, ConflictException;

    /**
     * Creates a new recommendation for the given group.
     *
     * @param groupId           of the group for which the recommendation should be created
     * @param numberOfCocktails the number of cocktails to be recommended
     * @param numberOfMenues    the number of menus to be recommended
     * @return the created recommendation
     */
    RecommendedMenuesDto createRecommendation(Long groupId, Integer numberOfCocktails, Integer numberOfMenues);

    /**
     * Updates the mixable cocktails if the ingredients change.
     *
     */
    void updateMixableCocktails();

    /**
     * Find menu by group id with ratings for each cocktail
     * to display for when you are host.
     *
     * @param groupId the id of the group
     * @return the menu
     */
    MenuCocktailsDetailViewDto findMenuDetailOfGroup(Long groupId) throws NotFoundException;
}
