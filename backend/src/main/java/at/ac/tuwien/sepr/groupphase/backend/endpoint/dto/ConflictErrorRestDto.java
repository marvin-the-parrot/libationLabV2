package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.util.List;

/**
 * Dto to send conflict error rest data to the frontend.
 */
public record ConflictErrorRestDto(
    String message,
    List<String> errors
) {
}
