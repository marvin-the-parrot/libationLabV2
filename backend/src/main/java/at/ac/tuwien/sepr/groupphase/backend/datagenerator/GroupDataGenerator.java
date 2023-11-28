package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationGroup;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.UserGroup;
import at.ac.tuwien.sepr.groupphase.backend.entity.UserGroupKey;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserGroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final UserGroupRepository userGroupRepository;

    public GroupDataGenerator(GroupRepository groupRepository, UserRepository userRepository, UserGroupRepository userGroupRepository) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.userGroupRepository = userGroupRepository;
    }

    @PostConstruct
    private void generateGroup() {
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
            ApplicationGroup groupTest = groupRepository.findById((long) 1).orElse(null);
            ApplicationUser user = userRepository.findById((long) 1).orElse(null);


            //TODO with UserGroup reference for join table
            UserGroup userGroup = UserGroup.UserGroupBuilder.userGroup()
                .withUserGroupKey(new UserGroupKey(user, groupTest))
                .withUser(user)
                .withGroup(groupTest)
                .withIsHost(true)
                .build();

            userGroupRepository.save(userGroup);

            userGroup.getUser();
        }
    }
}
