package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.GroupCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.GroupOverviewDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MessageCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserListGroupDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.GroupMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationGroup;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.UserGroup;
import at.ac.tuwien.sepr.groupphase.backend.entity.UserGroupKey;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserGroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.GroupService;
import at.ac.tuwien.sepr.groupphase.backend.service.MessageService;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import at.ac.tuwien.sepr.groupphase.backend.service.validators.GroupValidator;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Optional;

/**
 * Group service implementation.
 */
@Service
public class GroupServiceImpl implements GroupService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final MessageService messageService;
    private final GroupRepository groupRepository;
    private final GroupValidator validator;
    private final UserRepository userRepository;
    private final UserGroupRepository userGroupRepository;
    private final UserMapper userMapper;
    private final UserService userService;
    private final GroupMapper groupMapper;

    public GroupServiceImpl(GroupRepository groupRepository, GroupValidator validator, MessageService messageService, UserRepository userRepository,
                            UserGroupRepository userGroupRepository, UserMapper userMapper, UserService userService, GroupMapper groupMapper) {
        this.groupRepository = groupRepository;
        this.validator = validator;
        this.messageService = messageService;
        this.userRepository = userRepository;
        this.userGroupRepository = userGroupRepository;
        this.userMapper = userMapper;
        this.userService = userService;
        this.groupMapper = groupMapper;
    }

    @Override
    public ApplicationGroup findOne(Long id) {
        LOGGER.debug("Find group with id {}", id);
        Optional<ApplicationGroup> group = groupRepository.findById(id);
        if (group.isPresent()) {
            return group.get();
        } else {
            throw new NotFoundException("Could not find group");
        }
    }

    @Override
    @Transactional
    public void deleteGroup(Long groupId) throws ValidationException {
        LOGGER.debug("Delete group ({})", groupId);

        ApplicationGroup group = groupRepository.findById(groupId).orElse(null);
        if (group == null) {
            throw new NotFoundException("Could not find group");
        }

        String currentUserMail = SecurityContextHolder.getContext().getAuthentication().getName();

        validator.validateIsCurrentUserHost(userRepository, userGroupRepository, groupId, currentUserMail);

        // delete all user groups
        List<UserGroup> userGroups = userGroupRepository.findAllByApplicationGroup(group);
        userGroupRepository.deleteAll(userGroups);
        // delete group
        groupRepository.delete(group);
    }

    @Override
    @Transactional
    public void deleteMember(Long groupId, Long userId) throws ValidationException {
        LOGGER.debug("Remove member from group({}, {})", groupId, userId);

        // check if user exists
        Optional<ApplicationUser> userToRemove = userRepository.findById(userId);
        if (userToRemove.isEmpty()) {
            throw new NotFoundException("Could not find user");
        }

        String currentUserMail = SecurityContextHolder.getContext().getAuthentication().getName();

        // check if the user to remove is the current user or if the current user is the host of the group
        if (!userToRemove.get().getEmail().equals(currentUserMail)) {
            ApplicationUser currentUser = userRepository.findByEmail(currentUserMail);
            if (currentUser == null) {
                throw new NotFoundException("Could not find current user");
            }
            UserGroup userGroup = userGroupRepository.findById(new UserGroupKey(currentUser.getId(), groupId)).orElse(null);
            if (userGroup == null || !userGroup.isHost()) {
                throw new ValidationException("You are not allowed to remove this user from the group", List.of());
            }
        }

        // get the group
        ApplicationGroup group = groupRepository.findById(groupId).orElse(null);
        if (group == null) {
            throw new NotFoundException("Could not find group");
        }

        // get the user group and check if the user is in the group
        UserGroup toRemove = userGroupRepository.findById(new UserGroupKey(userId, groupId)).orElse(null);
        if (toRemove == null) {
            throw new NotFoundException("Could not find user in group");
        }
        // remove the user from the group
        userGroupRepository.delete(toRemove);

    }

    @Override
    public List<UserListDto> searchForMember(Long groupId) {
        LOGGER.debug("Search for member in group, by member name and group id {}", groupId);
        return userMapper.userToUserListDto(userGroupRepository.findUsersByGroupId(groupId));
    }

    @Override
    @Transactional
    public GroupCreateDto create(GroupCreateDto toCreate) throws ValidationException, ConflictException {
        LOGGER.trace("create({})", toCreate);
        // validate group:
        validator.validateForCreate(toCreate, userRepository);

        // build group entity and save it:
        ApplicationGroup group = ApplicationGroup.GroupBuilder.group().withName(toCreate.getName()).build();
        LOGGER.debug("saving group {}", group);
        ApplicationGroup saved = groupRepository.save(group);

        // save members in database:
        for (var member : toCreate.getMembers()) {
            boolean isHost = member.getId().equals(toCreate.getHost().getId()); // save host in database

            if (isHost) {
                // save host in database
                UserGroup newMember = UserGroup.UserGroupBuilder.userGroup().withUserGroupKey(new UserGroupKey(member.getId(), saved.getId()))
                    .withUser(userRepository.findById(member.getId()).orElse(null)).withGroup(groupRepository.findById(saved.getId()).orElse(null))
                    .withIsHost(true).build();
                userGroupRepository.save(newMember);
            } else {
                // send invitation to member

                /*ApplicationMessage message = ApplicationMessage.ApplicationMessageBuilder.message()
                    .withApplicationUser(userRepository.findById(member.getId()).orElse(null))
                    .withText("You were invited to drink with " + saved.getName())
                    .withGroupId(saved.getId())
                    .withIsRead(false)
                    .withSentAt(java.time.LocalDateTime.now())
                    .build();*/

                MessageCreateDto message = new MessageCreateDto();
                message.setUserId(member.getId());
                message.setGroupId(saved.getId());
                messageService.create(message);
            }

        }

        return applicationGroupToGroupCreateDto(saved);
    }

    @Override
    @Transactional
    public GroupCreateDto update(GroupCreateDto toUpdate) throws NotFoundException, ValidationException, ConflictException {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        LOGGER.trace("update({})", toUpdate);
        // validate group:
        validator.validateForUpdate(toUpdate, userRepository, userGroupRepository, currentUser);

        // build new group entity and save it:
        ApplicationGroup group = ApplicationGroup.GroupBuilder.group().withId(toUpdate.getId()).withName(toUpdate.getName()).build();
        LOGGER.debug("saving group {}", group);
        ApplicationGroup saved = groupRepository.save(group);

        // update members in database:
        List<UserGroup> existingUserGroups = userGroupRepository.findAllByApplicationGroup(group);

        // remove members that are not in the new group anymore
        for (var existingUserGroup : existingUserGroups) {
            boolean found = false;
            for (var member : toUpdate.getMembers()) {
                if (existingUserGroup.getUser().getId().equals(member.getId())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                userGroupRepository.delete(existingUserGroup);
            }
        }

        // add new members
        for (var member : toUpdate.getMembers()) {
            boolean found = false;
            for (var existingUserGroup : existingUserGroups) {
                if (existingUserGroup.getUser().getId().equals(member.getId())) {
                    found = true;
                    break;
                }
            }
            if (!found) {

                MessageCreateDto message = new MessageCreateDto();
                message.setUserId(member.getId());
                message.setGroupId(saved.getId());
                messageService.create(message);


            }
        }

        return applicationGroupToGroupCreateDto(saved);
    }

    @Override
    @Transactional
    public List<UserGroup> findGroupsByUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        LOGGER.trace("findGroupsByUser({})", email);
        // find groups in database
        return userGroupRepository.findAllByApplicationUser(userRepository.findByEmail(email));
    }

    @Override
    @Transactional
    public void makeMemberHost(Long groupId, Long userId) throws ValidationException {
        String currentUserMail = SecurityContextHolder.getContext().getAuthentication().getName();
        LOGGER.trace("makeMemberHost({}, {}, {})", groupId, userId, currentUserMail);

        // check if group exists
        ApplicationGroup group = groupRepository.findById(groupId).orElse(null);
        if (group == null) {
            throw new NotFoundException("Could not find group");
        }

        // check if current user is host of the group
        final UserGroup currentUserGroup = validator.validateIsCurrentUserHost(userRepository, userGroupRepository, groupId, currentUserMail);

        // check if user exists and is member of the group
        UserGroup makeHostUserGroup = userGroupRepository.findById(new UserGroupKey(userId, groupId)).orElse(null);
        if (makeHostUserGroup == null) {
            throw new NotFoundException("Could not find user in group");
        }

        // make user host
        makeHostUserGroup.setHost(true);
        userGroupRepository.save(makeHostUserGroup);
        // make current user member
        currentUserGroup.setHost(false);
        userGroupRepository.save(currentUserGroup);

    }

    @Override
    public GroupOverviewDto findGroupById(Long id) throws NotFoundException, ValidationException {

        // verify the user
        String currentUserMail = SecurityContextHolder.getContext().getAuthentication().getName();
        // get current user
        ApplicationUser currentUser = userRepository.findByEmail(currentUserMail);
        if (currentUser == null) {
            throw new NotFoundException("Could not find current user");
        }
        // get group
        ApplicationGroup group = groupRepository.findById(id).orElse(null);
        if (group == null) {
            throw new NotFoundException("Could not find group");
        }
        // check if user is member of the group
        UserGroup currentUserGroup = userGroupRepository.findById(new UserGroupKey(currentUser.getId(), id)).orElse(null);
        if (currentUserGroup == null) {
            throw new ValidationException("This action is not allowed", List.of("You are not a member of this group"));
        }

        // get members to build group
        List<UserListGroupDto> users = userService.findUsersByGroup(group);
        GroupOverviewDto groupOverviewDto = groupMapper.grouptToGroupOverviewDto(group);
        groupOverviewDto.setMembers(users.toArray(new UserListGroupDto[0]));

        //set host
        for (UserListGroupDto user : users) {
            if (user.isHost()) {
                groupOverviewDto.setHost(user);
            }
        }
        return groupOverviewDto;
    }


    /**
     * Converts a ApplicationGroup to a GroupCreateDto.
     *
     * @param group the group to convert
     * @return the converted group
     */
    public GroupCreateDto applicationGroupToGroupCreateDto(ApplicationGroup group) {

        GroupCreateDto groupCreateDto = new GroupCreateDto();
        groupCreateDto.setId(group.getId());
        groupCreateDto.setName(group.getName());

        List<UserGroup> userGroups = userGroupRepository.findAllByApplicationGroup(group);
        UserListDto[] members = new UserListDto[userGroups.size()];
        for (int i = 0; i < userGroups.size(); i++) {
            members[i] = userMapper.userToUserListDto(userGroups.get(i).getUser());
        }
        groupCreateDto.setMembers(members);

        //set host
        for (var user : userGroups) {
            if (user.isHost()) {
                groupCreateDto.setHost(userMapper.userToUserListDto(user.getUser()));
            }
        }
        return groupCreateDto;
    }
}
