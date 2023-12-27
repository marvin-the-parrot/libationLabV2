package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.IngredientListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PreferenceListDto;

import java.util.List;

/**
 * Service for Preference Entity.
 */
public interface PreferenceService {
    /**
     * Retrieve all stored ingredients, that match the given parameters.
     * The parameters may include a limit on the amount of results to return.
     *
     * @param searchParams parameters to search ingredients by
     * @return a stream of ingredients matching the parameters
     */
    List<PreferenceListDto> searchUserPreferences(String searchParams);
}
