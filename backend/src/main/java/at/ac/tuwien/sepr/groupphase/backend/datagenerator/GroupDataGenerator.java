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
import java.util.HashMap;
import java.util.Map;

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

            // <userId, groupId>
            Map<Long, Long> userGroupMap = new HashMap<Long, Long>();
            userGroupMap.put((long) 1, (long) 1);
            userGroupMap.put((long) 2, (long) 1);
            userGroupMap.put((long) 3, (long) 1);
            userGroupMap.put((long) 4, (long) 2);
            userGroupMap.put((long) 5, (long) 2);
            userGroupMap.put((long) 6, (long) 2);
            userGroupMap.put((long) 7, (long) 2);
            userGroupMap.put((long) 8, (long) 3);
            userGroupMap.put((long) 9, (long) 3);

            for (int i = 1; i <= userGroupMap.size(); i++) {
                ApplicationUser user = userRepository.findById((long) i).orElse(null);
                ApplicationGroup groupTest = groupRepository.findById(userGroupMap.get((long) i)).orElse(null);

                UserGroup userGroup = UserGroup.UserGroupBuilder.userGroup()
                    .withUserGroupKey(new UserGroupKey(user.getId(), groupTest.getId()))
                    .withUser(user)
                    .withGroup(groupTest)
                    .withIsHost(true)
                    .build();
                LOGGER.debug("saving userGroup {}", userGroup);
                userGroupRepository.save(userGroup);
            }
            System.out.println("test");

        }
    }
}
