package at.ac.tuwien.sepr.groupphase.backend.service.validators;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

@Component
public class UserValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public UserValidator() {
    }

    /**
     * Validates a message that should be created.
     *
     * @param toCreate the message to create
     * @throws ValidationException          if the create data given for the user
     *                                      is in itself incorrect (no name, name too long …)
     * @throws ConstraintViolationException if the create data given for the user
     *                                      is in conflict the data currently in the system (user already exists)
     */
    public void validateForCreate(UserCreateDto toCreate)
        throws ValidationException, ConstraintViolationException {

        LOGGER.trace("validateForCreate({})", toCreate);
        List<String> validationErrors = new ArrayList<>();

        if (toCreate.getEmail() == null) {
            validationErrors.add("No email specified");
        }

        if (toCreate.getEmail().length() > 255) {
            validationErrors.add("Email too long");
        }

        if (!toCreate.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            validationErrors.add("Email not valid");
        }

        if (toCreate.getName() == null) {
            validationErrors.add("No username specified");
        }

        if (toCreate.getName().length() > 255) {
            validationErrors.add("Username too long");
        }

        if (toCreate.getPassword().length() < 8) {
            validationErrors.add("Password too short");
        }

        List<String> constraintViolationErrors = new ArrayList<>();


        if (!validationErrors.isEmpty()) {
            throw new ValidationException("Validation of user for create failed", validationErrors);
        }

        if (!constraintViolationErrors.isEmpty()) {
            throw new ConstraintViolationException("Constraint violation of user for create failed", null, constraintViolationErrors.toString());
        }
    }

}
