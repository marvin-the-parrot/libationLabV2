package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.util.List;

/**
 * Dto to send validation errors to the frontend.
 */
public record ValidationErrorRestDto(
    String message,
    List<String> errors
) {
}
