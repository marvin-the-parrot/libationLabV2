package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationMessage;
import at.ac.tuwien.sepr.groupphase.backend.repository.MessageRepository;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;

import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Message Data Generator.
 */
@Profile("generateData")
@Component
public class MessageDataGenerator {

    private static final Logger LOGGER =
        LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int NUMBER_OF_MESSAGES_TO_GENERATE = 2;

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public MessageDataGenerator(MessageRepository messageRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    @PostConstruct
    private void generateMessage() {
        if (messageRepository.findAll().size() > 0) {
            LOGGER.debug("message already generated");
        } else {
            LOGGER.debug("generating {} message entries", NUMBER_OF_MESSAGES_TO_GENERATE);

            for (int i = 0; i < NUMBER_OF_MESSAGES_TO_GENERATE; i++) {
                ApplicationMessage message = ApplicationMessage.ApplicationMessageBuilder.message()
                    .withId((long) i)
                    .withApplicationUser(userRepository.findByEmail("user1@email.com"))
                    .withText("You were invited to drink with Group" + 3)
                    .withGroupId(3L)
                    .withIsRead(false)
                    .withSentAt(LocalDateTime.now())
                    .build();
                LOGGER.debug("saving message {}", message);
                messageRepository.save(message);
            }
        }
    }

}
