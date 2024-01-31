package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PreferenceListDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;

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

    /**
     * Retrieve all stored preferences, which are associated with a user.
     *
     * @return a stream of preferences belonging to a user
     */
    List<PreferenceListDto> getUserPreferences();

    /**
     * Add preferences to user.
     *
     * @param preferenceListDto preferences to add
     */
    List<PreferenceListDto> addPreferencesToUser(PreferenceListDto[] preferenceListDto) throws ConflictException;

}
