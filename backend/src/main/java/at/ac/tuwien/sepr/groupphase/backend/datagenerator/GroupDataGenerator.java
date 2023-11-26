package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationGroup;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

/**
 * Group Data Generator.
 */
@Profile("generateData")
@Component
public class GroupDataGenerator {

    private static final Logger LOGGER =
        LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int NUMBER_OF_GROUPS_TO_GENERATE = 5;

    private final GroupRepository groupRepository;

    public GroupDataGenerator(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    @PostConstruct
    private void generateMessage() {
        if (groupRepository.findAll().size() > 0) {
            LOGGER.debug("group already generated");
        } else {
            LOGGER.debug("generating {} group entries", NUMBER_OF_GROUPS_TO_GENERATE);
            //TODO with UserGroup reference for join table
            for (int i = 0; i < NUMBER_OF_GROUPS_TO_GENERATE; i++) {
                ApplicationGroup group = ApplicationGroup.GroupBuilder.group()
                    .withId((long) i)
                    .withName("Group" + i)
                    .build();
                LOGGER.debug("saving group {}", group);
                groupRepository.save(group);
            }
        }
    }
}
