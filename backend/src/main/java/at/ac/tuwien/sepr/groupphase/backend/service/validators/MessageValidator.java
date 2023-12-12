package at.ac.tuwien.sepr.groupphase.backend.service.validators;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MessageCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MessageDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserListDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationMessage;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.MessageRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.GroupService;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.CustomUserDetailService;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

@Component
public class MessageValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final CustomUserDetailService userService;
    private final GroupService groupService;
    private final MessageRepository messageRepository;
    private final GroupRepository groupRepository;

    @Autowired
    public MessageValidator(CustomUserDetailService userService, GroupService groupService, MessageRepository messageRepository, GroupRepository groupRepository) {
        this.userService = userService;
        this.groupService = groupService;
        this.messageRepository = messageRepository;
        this.groupRepository = groupRepository;
    }

    /**
     * Validates a message that should be created.
     *
     * @param toCreate the message to create
     * @throws ValidationException          if the create data given for the message
     *                                      is in itself incorrect (no name, name too long …)
     * @throws ConstraintViolationException if the create data given for the message
     *                                      is in conflict the data currently in the system (user, group does not exist)
     */
    public void validateForCreate(MessageCreateDto toCreate)
        throws ValidationException, ConstraintViolationException {

        LOGGER.trace("validateForCreate({})", toCreate);
        List<String> validationErrors = new ArrayList<>();
        List<String> constraintViolationErrors = new ArrayList<>();

        if (toCreate.getUserId() == null) {
            validationErrors.add("No user ID given");
        }

        if (toCreate.getGroupId() == null) {
            validationErrors.add("No group ID given");
        }

        ApplicationUser user = userService.findApplicationUserById(toCreate.getUserId());

        if (user == null) {
            constraintViolationErrors.add("User does not exist");
        }

        if (groupService.findOne(toCreate.getGroupId()) == null) {
            constraintViolationErrors.add("Group does not exist");
        }

        List<ApplicationMessage> messages = messageRepository.findAllByApplicationUserAndGroupId(user, toCreate.getGroupId());

        if (!messages.isEmpty()) {
            validationErrors.add(String.format("You already invited user %s to the group", user.getName()));
        }

        List<UserListDto> groupUsers = groupService.searchForMember(toCreate.getGroupId());

        for (UserListDto groupUser : groupUsers) {
            if (groupUser.getId().equals(toCreate.getUserId())) {
                validationErrors.add(String.format("User %s is already in the group", user.getName()));
            }
        }

        if (!validationErrors.isEmpty()) {
            throw new ValidationException("Validation of message for create failed", validationErrors);
        }

        if (!constraintViolationErrors.isEmpty()) {
            throw new ConstraintViolationException("Constraint violation of message for create failed", null, constraintViolationErrors.toString());
        }
    }

    /**
     * Validates a message that should be updated.
     *
     * @param message the message to update
     * @throws ValidationException if the update data given for the message
     *                             is in itself incorrect (no name, name too long …)
     * @throws ConflictException   if the update data given for the message
     *                             is in conflict the data currently in the system (group does not exist)
     */
    public void validateForUpdate(MessageDetailDto message)
        throws ValidationException, ConflictException {

        LOGGER.trace("validateForUpdate({})", message);
        List<String> validationErrors = new ArrayList<>();
        List<String> conflictErrors = new ArrayList<>();

        if (message.getGroup() == null) {
            conflictErrors.add("No group given");
        }

        if (!message.getIsRead()) {
            validationErrors.add("Message is not read");
        }

        if (!validationErrors.isEmpty()) {
            throw new ValidationException("Validation of message for update failed", validationErrors);
        }

        if (!conflictErrors.isEmpty()) {
            throw new ConflictException("Constraint violation of message for update failed", conflictErrors);
        }
    }

}
