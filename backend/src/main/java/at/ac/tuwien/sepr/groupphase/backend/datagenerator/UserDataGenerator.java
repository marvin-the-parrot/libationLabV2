package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Message;
import at.ac.tuwien.sepr.groupphase.backend.repository.MessageRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;

@Profile("generateData")
@Component
public class UserDataGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final UserRepository userRepository;

    public UserDataGenerator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostConstruct
    private void generateUsers() {
        if (userRepository.findAll().size() > 0) {
            LOGGER.debug("users already found deleting...");
            userRepository.deleteAll();
            LOGGER.debug("users deleted");
        }

        userRepository.save(new ApplicationUser("User1", "user@email.com", "password"));
        userRepository.save(new ApplicationUser("User2", "user2@email.com", "password"));
        userRepository.save(new ApplicationUser("User3", "user3@email.com", "password"));

    }

}
