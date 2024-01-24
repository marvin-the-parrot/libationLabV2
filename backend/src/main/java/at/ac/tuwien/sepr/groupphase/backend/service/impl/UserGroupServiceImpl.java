package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationGroup;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.UserGroup;
import at.ac.tuwien.sepr.groupphase.backend.entity.UserGroupKey;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserGroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.FeedbackService;
import at.ac.tuwien.sepr.groupphase.backend.service.GroupService;
import at.ac.tuwien.sepr.groupphase.backend.service.UserGroupService;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import jakarta.transaction.Transactional;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;

@Service
public class UserGroupServiceImpl implements UserGroupService {

    private static final Logger LOGGER =
        LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserGroupRepository userGroupRepository;
    private final UserService userService;
    private final GroupService groupService;
    private final FeedbackService feedbackService;

    @Autowired
    public UserGroupServiceImpl(UserGroupRepository userGroupRepository, UserService userService, GroupService groupService, FeedbackService feedbackService) {
        this.userGroupRepository = userGroupRepository;
        this.userService = userService;
        this.groupService = groupService;
        this.feedbackService = feedbackService;
    }

    @Transactional
    @Override
    public void create(Long groupId) throws ConstraintViolationException, NotFoundException {
        LOGGER.debug("Create new userGroup {}", groupId);
        ApplicationUser user = userService.findApplicationUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        ApplicationGroup group = groupService.findOne(groupId);
        if (user == null || group == null) {
            throw new ConstraintViolationException("User or group does not exist", null, "user or group");
        }

        UserGroup userGroup = UserGroup.UserGroupBuilder.userGroup()
            .withUserGroupKey(new UserGroupKey(user.getId(), group.getId()))
            .withUser(user)
            .withGroup(group)
            .withIsHost(false)
            .build();
        LOGGER.debug("saving userGroup {}", userGroup);
        userGroupRepository.save(userGroup);

        feedbackService.createFeedbackRelationsForNewUser(group, user);
    }
}
