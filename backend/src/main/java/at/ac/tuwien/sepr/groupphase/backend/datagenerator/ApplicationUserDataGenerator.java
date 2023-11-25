package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

@Profile("generateDataUser")
@Component
public class ApplicationUserDataGenerator {

    private static final Logger LOGGER =
        LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int NUMBER_OF_USERS_TO_GENERATE = 5;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ApplicationUserDataGenerator(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    private void generateUser() {
        LOGGER.debug("generating {} user entries", NUMBER_OF_USERS_TO_GENERATE);
        for (int i = 0; i < NUMBER_OF_USERS_TO_GENERATE; i++) {
            ApplicationUser applicationUser = ApplicationUser.ApplicationUserBuilder.applicationUser()
                .withId((long) i)
                .withName("User" + i)
                .withEmail("user" + i + "@email.com")
                .withPassword(passwordEncoder.encode("password"))
                .build();
            userRepository.save(applicationUser);
        }
    }

}
