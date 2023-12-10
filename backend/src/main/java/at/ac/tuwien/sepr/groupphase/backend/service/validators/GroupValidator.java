package at.ac.tuwien.sepr.groupphase.backend.service.validators;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.GroupCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserListDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.UserGroup;
import at.ac.tuwien.sepr.groupphase.backend.entity.UserGroupKey;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserGroupRepository;
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
        List<String> conflictErrors = checkForNonexistentMembers(userRepository, toCreate);

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
    public void validateForUpdate(GroupCreateDto toUpdate, UserRepository userRepository, UserGroupRepository userGroupRepository, String currentUserMail)
        throws ValidationException, ConflictException {
        LOGGER.trace("validateForUpdate({})", toUpdate);

        // check for validation errors
        List<String> validationErrors = getValidationErrors(toUpdate);
        // validate group id
        if (toUpdate.getId() == 0) {
            validationErrors.add("Group id must not be 0");
        }
        if (!validationErrors.isEmpty()) {
            throw new ValidationException("Validation of group for update failed", validationErrors);
        }

        validateIsCurrentUserHost(userRepository, userGroupRepository, toUpdate.getId(), currentUserMail);

        // check for Conflicts
        List<String> conflictErrors = checkForNonexistentMembers(userRepository, toUpdate);

        if (!conflictErrors.isEmpty()) {
            throw new ConflictException("Validation of group for create failed", conflictErrors);
        }
    }

    /**
     * Validates if the current user is the host of the group.
     *
     * @param userRepository      the user repository to get the user via his email
     * @param userGroupRepository the user group repository to get the user group via the user id and the group id
     * @param groupId             the id of the group
     * @param currentUserMail     the email of the current user
     * @return the user group of the current user if he is the host
     * @throws ValidationException if the current user is not the host of the group
     */
    public UserGroup validateIsCurrentUserHost(UserRepository userRepository, UserGroupRepository userGroupRepository, long groupId, String currentUserMail)
        throws ValidationException {
        ApplicationUser currentUser = userRepository.findByEmail(currentUserMail);
        if (currentUser == null) {
            throw new NotFoundException("Could not find current user");
        }
        UserGroup currentUserGroup = userGroupRepository.findById(new UserGroupKey(currentUser.getId(), groupId)).orElse(null);
        if (currentUserGroup == null || !currentUserGroup.isHost()) {
            throw new ValidationException("This action is not allowed", List.of("You are not the host of this group"));
        }
        return currentUserGroup;
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
        } else if (group.getName().length() > 255) {
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

    /**
     * Checks if the members of the group exist.
     *
     * @param userRepository the user repository to get the users
     * @param group          the group dto to check
     * @return a list of conflict errors (empty if no errors)
     */
    private List<String> checkForNonexistentMembers(UserRepository userRepository, GroupCreateDto group) {
        List<String> conflictErrors = new LinkedList<>();

        ApplicationUser host = userRepository.findById(group.getHost().getId()).orElse(null);
        if (host == null) {
            conflictErrors.add("Group host does not exist");
        }
        for (var member : group.getMembers()) {
            ApplicationUser user = userRepository.findById(member.getId()).orElse(null);
            if (user == null) {
                conflictErrors.add("Group member " + member.getName() + " does not exist");
            }
        }

        return conflictErrors;
    }
}
