package at.ac.tuwien.sepr.groupphase.backend.service.validators;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MessageCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MessageDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationMessage;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.MessageRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserGroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.CustomUserDetailService;
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
    private final MessageRepository messageRepository;
    private final UserGroupRepository userGroupRepository;

    @Autowired
    public MessageValidator(CustomUserDetailService userService, MessageRepository messageRepository, UserGroupRepository userGroupRepository) {
        this.userService = userService;
        this.messageRepository = messageRepository;
        this.userGroupRepository = userGroupRepository;
    }

    /**
     * Validates a message that should be created.
     *
     * @param toCreate the message to create
     * @throws ValidationException          if the create data given for the message
     *                                      is in itself incorrect (no name, name too long …)
     */
    public void validateForCreate(MessageCreateDto toCreate)
        throws ValidationException {

        LOGGER.trace("validateForCreate({})", toCreate);
        List<String> validationErrors = new ArrayList<>();

        if (toCreate.getUserId() == null) {
            validationErrors.add("No user ID given");
        }

        if (toCreate.getGroupId() == null) {
            validationErrors.add("No group ID given");
        }

        ApplicationUser user = userService.findApplicationUserById(toCreate.getUserId());

        List<ApplicationMessage> messages = messageRepository.findAllByApplicationUserAndGroupId(user, toCreate.getGroupId());

        if (!messages.isEmpty()) {
            for (ApplicationMessage message : messages) {
                if (!message.getIsRead()) {
                    assert user != null;
                    validationErrors.add(String.format("You already invited user %s to the group", user.getName()));
                }
            }
        }

        List<ApplicationUser> groupUsers = userGroupRepository.findUsersByGroupId(toCreate.getGroupId());

        for (ApplicationUser groupUser : groupUsers) {
            if (groupUser.getId().equals(toCreate.getUserId())) {
                assert user != null;
                validationErrors.add(String.format("User %s is already in the group", user.getName()));
            }
        }

        if (!validationErrors.isEmpty()) {
            throw new ValidationException("Validation of message for create failed", validationErrors);
        }
    }

    /**
     * Validates a message that should be updated.
     *
     * @param message the message to update
     * @throws ValidationException if the update data given for the message
     *                             is in itself incorrect (no name, name too long …)
     */
    public void validateForUpdate(MessageDetailDto message)
        throws ValidationException {

        LOGGER.trace("validateForUpdate({})", message);
        List<String> validationErrors = new ArrayList<>();

        if (!message.getIsRead()) {
            validationErrors.add("Message is not read");
        }

        if (!validationErrors.isEmpty()) {
            throw new ValidationException("Validation of message for update failed", validationErrors);
        }
    }

}
