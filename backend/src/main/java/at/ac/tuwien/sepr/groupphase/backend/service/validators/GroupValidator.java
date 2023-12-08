package at.ac.tuwien.sepr.groupphase.backend.service.validators;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.GroupCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserListDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

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
     * @throws ValidationException if the create data given for the group
     *                             is in itself incorrect (no name, name too long …)
     * @throws ConflictException   if the create data given for the group
     *                             is in conflict the data currently in the system (group member does not exist, …)
     */
    public void validateForCreate(GroupCreateDto toCreate, UserRepository userRepository) throws ValidationException, ConflictException {
        LOGGER.trace("validateForCreate({})", toCreate);

        // check for validation errors
        List<String> validationErrors = getValidationErrors(toCreate);

        if (!validationErrors.isEmpty()) {
            throw new ValidationException("Validation of group for create failed", validationErrors);
        }

        // check for Conflicts
        List<String> conflictErrors = new LinkedList<>();
        ApplicationUser host = userRepository.findById(toCreate.getHost().getId()).orElse(null);
        if (host == null) {
            conflictErrors.add("Group host does not exist");
        }
        for (var member : toCreate.getMembers()) {
            ApplicationUser user = userRepository.findById(member.getId()).orElse(null);
            if (user == null) {
                conflictErrors.add("Group member " + member.getName() + " does not exist");
            }
        }

        if (!conflictErrors.isEmpty()) {
            throw new ConflictException("Validation of group for create failed", conflictErrors);
        }

    }

    /**
     * Validates a group that should get updated.
     *
     * @param toUpdate the group to update
     * @throws ValidationException if the update data given for the group
     *                             is in itself incorrect (no name, name too long …)
     * @throws ConflictException   if the update data given for the group
     *                             is in conflict the data currently in the system (group member does not exist, …)
     */
    public void validateForUpdate(GroupCreateDto toUpdate) throws ValidationException, ConflictException {
        LOGGER.trace("validateForUpdate({})", toUpdate);

        List<String> validationErrors = getValidationErrors(toUpdate);
        // todo: validate remaining data (that does not need to be validated for create. eg. group id)

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
    private List<String> getValidationErrors(GroupCreateDto group) {
        List<String> validationErrors = new LinkedList<>();

        if (group.getName() == null || group.getName().isEmpty()) {
            validationErrors.add("Group name must not be empty");
        }
        if (group.getName().length() > 255) {
            validationErrors.add("Group name must not be longer than 255 characters");
        }
        if (group.getHost() == null) {
            validationErrors.add("Group host must not be null");
        }
        if (group.getMembers() == null) {
            validationErrors.add("Group members must not be null");
        } else {
            List<UserListDto> checkedMembers = new LinkedList<>();
            boolean foundHost = false;
            for (var member : group.getMembers()) {
                if (member == null) {
                    validationErrors.add("Group member must not be null");
                    continue;
                }
                if (member.getId() == null) {
                    validationErrors.add("Group member id must not be null");
                }
                if (member.getName() == null || member.getName().isEmpty()) {
                    validationErrors.add("Group member name must not be empty");
                } else
                    if (member.getName().length() > 255) {
                        validationErrors.add("Group member name must not be longer than 255 characters");
                    }
                if (group.getHost() != null && Objects.equals(member.getId(), group.getHost().getId())) {
                    if (foundHost) {
                        validationErrors.add("Group host must not be added twice");
                    }
                    foundHost = true;
                }
                if (checkedMembers.contains(member)) {
                    validationErrors.add("Group member must not be added twice");
                }
                checkedMembers.add(member);
            }
            if (!foundHost) {
                validationErrors.add("Group host must be in group members");
            }
        }

        return validationErrors;
    }
}
