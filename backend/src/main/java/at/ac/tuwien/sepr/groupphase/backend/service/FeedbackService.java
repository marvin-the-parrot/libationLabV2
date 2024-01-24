package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CocktailFeedbackDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CocktailFeedbackHostDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.FeedbackCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MenuCocktailsDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationGroup;
import at.ac.tuwien.sepr.groupphase.backend.entity.Cocktail;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;

import java.util.List;
import java.util.Set;

public interface FeedbackService {

    List<CocktailFeedbackHostDto> getRatings(Long groupId) throws NotFoundException;

    void create(FeedbackCreateDto feedbackToCreate) throws NotFoundException;

    void update(CocktailFeedbackDto feedbackToUpdate) throws NotFoundException;

    void deleteFeedbackRelationsAtCocktailChange(Long groupId, Long userId) throws NotFoundException;

}
