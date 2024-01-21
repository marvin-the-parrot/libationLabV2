package at.ac.tuwien.sepr.groupphase.backend.exception;

/**
 * Not found exception.
 */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }

}
