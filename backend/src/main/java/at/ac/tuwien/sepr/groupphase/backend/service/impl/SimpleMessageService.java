package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MessageCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MessageDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MessageSetReadDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationGroup;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationMessage;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.MessageRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.MessageService;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import at.ac.tuwien.sepr.groupphase.backend.service.validators.MessageValidator;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple message service.
 */
@Service
public class SimpleMessageService implements MessageService {

    private static final Logger LOGGER =
        LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final MessageRepository messageRepository;
    private final UserService userService;
    private final GroupRepository groupRepository;
    private final MessageValidator validator;

    @Autowired
    public SimpleMessageService(MessageRepository messageRepository, UserService userService, GroupRepository groupRepository, MessageValidator validator) {
        this.messageRepository = messageRepository;
        this.userService = userService;
        this.groupRepository = groupRepository;
        this.validator = validator;
    }

    @Override
    public long getUnreadMessageCount() {
        LOGGER.debug("Count unread messages");
        ApplicationUser user = userService.findApplicationUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        return messageRepository.countByApplicationUserAndIsRead(user, false);
    }

    @Transactional
    @Override
    public List<ApplicationMessage> findAll() throws NotFoundException {
        LOGGER.debug("Find all messages");
        ApplicationUser user = userService.findApplicationUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        return messageRepository.findAllByApplicationUserOrderByIsReadAscSentAtDesc(user);
    }

    @Override
    public ApplicationMessage create(MessageCreateDto message) throws NotFoundException, ValidationException {
        LOGGER.debug("Publish new message {}", message);
        validator.validateForCreate(message);
        ApplicationGroup group = groupRepository.findById(message.getGroupId()).orElse(null);
        if (group == null) {
            throw new NotFoundException("Could not find group");
        }
        ApplicationUser user = userService.findApplicationUserById(message.getUserId());

        ApplicationMessage applicationMessage = ApplicationMessage.ApplicationMessageBuilder.message()
            .withApplicationUser(user)
            .withText("You were invited to drink with " + group.getName())
            .withGroupId(message.getGroupId())
            .withIsRead(false)
            .withSentAt(LocalDateTime.now())
            .build();
        return messageRepository.save(applicationMessage);
    }

    @Override
    public ApplicationMessage update(MessageDetailDto toUpdate) throws NotFoundException, ValidationException {
        LOGGER.debug("Update message {}", toUpdate);
        ApplicationMessage myMessage = messageRepository.findById(toUpdate.getId()).orElse(null);
        if (myMessage == null) {
            throw new NotFoundException(String.format("Could not find message from group %s sent at %s",
                toUpdate.getGroup().getName(), toUpdate.getSentAt()));
        }
        ApplicationGroup group = groupRepository.findById(toUpdate.getGroup().getId()).orElse(null);
        if (group == null) {
            throw new NotFoundException("Could not find group");
        }

        toUpdate.setIsRead(true);
        validator.validateForUpdate(toUpdate);
        myMessage.setIsRead(toUpdate.getIsRead());
        return messageRepository.save(myMessage);
    }

    @Override
    public void delete(Long messageId) throws NotFoundException {
        LOGGER.debug("Delete message with id {}", messageId);
        if (!messageRepository.existsById(messageId)) {
            throw new NotFoundException(String.format("Could not find message with id %s",
                messageId));
        }
        messageRepository.deleteById(messageId);
    }

    @Transactional
    @Override
    public void markAllAsRead(MessageSetReadDto[] messagesToSetRead) throws NotFoundException {
        LOGGER.debug("Mark messages as read {}", (Object) messagesToSetRead);

        List<Long> messageIds = new ArrayList<>();
        for (MessageSetReadDto message : messagesToSetRead) {
            messageIds.add(message.getId());
        }

        List<ApplicationMessage> messages = messageRepository.findByIdIn(messageIds);

        if (messages.isEmpty()) {
            throw new NotFoundException("Could not find messages");
        }

        for (int i = 0; i < messages.size(); i++) {
            messages.get(i).setIsRead(messagesToSetRead[i].getIsRead());
        }

        messageRepository.saveAll(messages);
    }

}
