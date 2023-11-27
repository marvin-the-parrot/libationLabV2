package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MessageDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationMessage;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.MessageRepository;
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
    private GroupRepository groupRepository;

    public SimpleMessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public List<ApplicationMessage> findAll() {
        LOGGER.debug("Find all messages");

        return messageRepository.findAllByOrderBySentAtDesc();
    }

    @Override
    public ApplicationMessage findById(Long id) {
        LOGGER.debug("Find message by id {}", id);

        return messageRepository.findById(id).orElseThrow();
    }

    @Override
    public ApplicationMessage save(ApplicationMessage applicationMessage) {
        LOGGER.debug("Publish new message {}", applicationMessage);
        applicationMessage.setIsRead(false);
        applicationMessage.setSentAt(LocalDateTime.now());
        return messageRepository.save(applicationMessage);
    }

}
