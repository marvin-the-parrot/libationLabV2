package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.Preference;
import at.ac.tuwien.sepr.groupphase.backend.repository.PreferenceRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

/**
 * Preference Data Generator.
 */
@Profile("generateData")
@Component
public class PreferenceDataGenerator {

    private static final Logger LOGGER =
        LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final int NUMBER_OF_PREFERENCES_TO_GENERATE = 10;
    private final PreferenceRepository preferenceRepository;

    public PreferenceDataGenerator(PreferenceRepository preferenceRepository) {
        this.preferenceRepository = preferenceRepository;
    }

    @PostConstruct
    public void generatePreference() {
        if (!preferenceRepository.findAll().isEmpty()) {
            LOGGER.debug("preference already generated");
        } else {
            LOGGER.debug("generating {} preference entries", NUMBER_OF_PREFERENCES_TO_GENERATE);
            for (int i = 0; i < NUMBER_OF_PREFERENCES_TO_GENERATE; i++) {
                Preference preference = Preference.PreferencesBuilder.preferences()
                    .withId((long) i)
                    .withName("Preference" + i)
                    .build();
                preferenceRepository.save(preference);
            }
        }
    }
}
