package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CocktailFeedbackDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.FeedbackCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationGroup;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;

public interface FeedbackService {

    /**
     * Create a new feedback for a cocktail.
     *
     * @param feedbackToCreate the feedback to create
     * @throws NotFoundException if the cocktail does not exist or user does not exist
     */
    void createFeedbackRelations(FeedbackCreateDto feedbackToCreate) throws NotFoundException;

    /**
     * Creates a new feedback Relation for a new User.
     *
     * @param group the group where the user is new
     * @param user the new user in the group
     * @throws NotFoundException if the group or user does not exist
     */
    void createFeedbackRelationsForNewUser(ApplicationGroup group, ApplicationUser user) throws NotFoundException;

    /**
     * Update a feedback for a cocktail.
     *
     * @param feedbackToUpdate the feedback to update
     * @throws NotFoundException if user, cocktail, group or feedback does not exist
     */
    void updateRatings(CocktailFeedbackDto feedbackToUpdate) throws NotFoundException;

    /**
     * delete a feedback for a cocktail.
     *
     * @param groupId the group where the feedback is deleted
     * @param userId the user associated with the feedback
     * @throws NotFoundException if group or user does not exist
     */
    void deleteFeedbackRelationsAtCocktailChange(Long groupId, Long userId) throws NotFoundException;

}
