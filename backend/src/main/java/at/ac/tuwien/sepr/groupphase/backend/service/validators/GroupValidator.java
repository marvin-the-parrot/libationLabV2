package at.ac.tuwien.sepr.groupphase.backend.service.validators;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.GroupDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.LinkedList;
import java.util.List;

/**
 * Validates requests for the group endpoint.
 */
@Component
public class GroupValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


    /**
     * Validates a group that should be created.
     *
     * @param toCreate the group to create
     * @throws ValidationException if the create data given for the group is in itself incorrect (no name, name too long …)
     * @throws ConflictException   if the create data given for the group is in conflict the data currently in the system (group member does not exist, …)
     */
    public void validateForCreate(GroupDetailDto toCreate) throws ValidationException, ConflictException {
        LOGGER.trace("validateForCreate({})", toCreate);

        List<String> validationErrors = getValidationErrors(toCreate);

        if (!validationErrors.isEmpty()) {
            throw new ValidationException("Validation of group for create failed", validationErrors);
        }

        // todo: check for possible conflicts (eg. group member does not exist)

    }

    /**
     * Validates a group that should get updated.
     *
     * @param toUpdate the group to update
     * @throws ValidationException if the update data given for the group is in itself incorrect (no name, name too long …)
     * @throws ConflictException  if the update data given for the group is in conflict the data currently in the system (group member does not exist, …)
     */
    public void validateForUpdate(GroupDetailDto toUpdate) throws ValidationException, ConflictException {
        LOGGER.trace("validateForUpdate({})", toUpdate);

        List<String> validationErrors = getValidationErrors(toUpdate);

        if (!validationErrors.isEmpty()) {
            throw new ValidationException("Validation of group for update failed", validationErrors);
        }

        // todo: check for possible conflicts (eg. group member does not exist)
    }

    /**
     * Validates a group.
     *
     * @param group the group to validate
     * @return a list of validation errors (empty if no errors)
     */
    private List<String> getValidationErrors(GroupDetailDto group) {
        List<String> validationErrors = new LinkedList<>();

        if (group.getName() == null || group.getName().isEmpty()) {
            validationErrors.add("Group name must not be empty");
        }
        if (group.getName().length() > 255) { // todo: check if this is the correct length
            validationErrors.add("Group name must not be longer than 255 characters");
        }

        // todo: validate remaining data


        return validationErrors;
    }
}
