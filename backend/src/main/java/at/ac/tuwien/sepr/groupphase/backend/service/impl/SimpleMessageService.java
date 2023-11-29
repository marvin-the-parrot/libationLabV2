package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MessageCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MessageDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationMessage;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.MessageRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.MessageService;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.List;

import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Simple message service.
 */
@Service
public class SimpleMessageService implements MessageService {

    private static final Logger LOGGER =
        LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final MessageRepository messageRepository;

    private final UserService userService;

    @Autowired
    public SimpleMessageService(MessageRepository messageRepository, UserService userService) {
        this.messageRepository = messageRepository;
        this.userService = userService;
    }

    @Override
    public List<ApplicationMessage> findAll() {
        LOGGER.debug("Find all messages");
        ApplicationUser user = userService.findApplicationUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        return messageRepository.findAllByApplicationUserOrderByIsReadAscSentAtDesc(user);
    }

    @Override
    public ApplicationMessage create(MessageCreateDto message) {
        LOGGER.debug("Publish new message {}", message);
        ApplicationMessage applicationMessage = ApplicationMessage.ApplicationMessageBuilder.message()
            .withApplicationUser(userService.findApplicationUserById(message.getUserId()))
            .withGroupId(message.getGroupId())
            .withIsRead(false)
            .withSentAt(LocalDateTime.now())
            .build();
        return messageRepository.save(applicationMessage);
    }

    @Override
    public ApplicationMessage update(MessageDetailDto toUpdate) throws NotFoundException, ValidationException, ConflictException {
        LOGGER.debug("Update message {}", toUpdate);
        ApplicationMessage myMessage = messageRepository.findById(toUpdate.getId()).orElseThrow();
        myMessage.setIsRead(true);
        return messageRepository.save(myMessage);
    }

    @Override
    public void delete(Long messageId) {
        LOGGER.debug("Delete message with id {}", messageId);
        messageRepository.deleteById(messageId);
    }

}
