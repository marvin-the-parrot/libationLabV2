package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MessageCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MessageDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationMessage;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.MessageRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.MessageService;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Simple message service.
 */
@Service
public class SimpleMessageService implements MessageService {

    private static final Logger LOGGER =
        LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    public SimpleMessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public List<ApplicationMessage> findAll() {
        LOGGER.debug("Find all messages");
        return messageRepository.findAllByOrderByIsReadAscSentAtDesc();
    }

    @Override
    public ApplicationMessage save(MessageCreateDto message) {
        LOGGER.debug("Publish new message {}", message);
        ApplicationMessage applicationMessage = ApplicationMessage.ApplicationMessageBuilder.message()
            .withApplicationUser(userRepository.findByEmail("user1@email.com"))
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

}
