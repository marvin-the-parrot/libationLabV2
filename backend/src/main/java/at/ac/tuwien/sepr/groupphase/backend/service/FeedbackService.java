package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CocktailFeedbackDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.FeedbackCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;

public interface FeedbackService {

    void create(FeedbackCreateDto feedbackToCreate) throws NotFoundException;

    void update(CocktailFeedbackDto feedbackToUpdate) throws NotFoundException;
}
