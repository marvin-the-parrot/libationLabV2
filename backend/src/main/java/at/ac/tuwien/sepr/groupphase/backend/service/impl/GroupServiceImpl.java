package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.GroupCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserListDto;
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
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import at.ac.tuwien.sepr.groupphase.backend.service.validators.GroupValidator;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final UserService userService;
    @Autowired
    private final GroupRepository groupRepository;
    private final GroupValidator validator;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserGroupRepository userGroupRepository;
    @Autowired
    private UserMapper userMapper;

    public GroupServiceImpl(UserService userService, GroupRepository groupRepository, GroupValidator validator) {
        this.userService = userService;
        this.groupRepository = groupRepository;
        this.validator = validator;
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
    public void deleteGroup(Long groupId, String currentUserMail) throws ValidationException {
        LOGGER.debug("Delete group ({})", groupId);

        ApplicationUser currentUser = userRepository.findByEmail(currentUserMail);
        if (currentUser == null) {
            throw new NotFoundException("Could not find current user");
        }

        ApplicationGroup group = groupRepository.findById(groupId).orElse(null);
        if (group == null) {
            throw new NotFoundException("Could not find group");
        }

        UserGroup userGroup = userGroupRepository.findById(new UserGroupKey(currentUser.getId(), groupId)).orElse(null);
        if (userGroup == null || !userGroup.isHost()) {
            throw new ValidationException("You are not allowed to delete this group", List.of("You are not the host of this group"));
        }

        // delete all user groups
        List<UserGroup> userGroups = userGroupRepository.findAllByApplicationGroup(group);
        userGroupRepository.deleteAll(userGroups);
        // delete group
        groupRepository.delete(group);
    }

    @Override
    public void deleteMember(Long groupId, Long userId, String currentUserMail) throws ValidationException {
        LOGGER.debug("Remove member from group({}, {})", groupId, userId);

        // check if user exists
        Optional<ApplicationUser> userToRemove = userRepository.findById(userId);
        if (userToRemove.isEmpty()) {
            throw new NotFoundException("Could not find user");
        }

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

        // var groupMembers = group.getMembers();
        // groupMembers.removeIf(member -> member.getUser().getId().equals(userId));
        // group.setMembers(groupMembers);
        // groupRepository.save(group);

    }

    @Override
    public List<UserListDto> searchForMember(Long groupId) {
        LOGGER.debug("Search for member in group, by member name and group id {}", groupId);
        return userMapper.userToUserListDto(userGroupRepository.findUsersByGroupId(groupId));
    }

    @Override
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

            UserGroup newMember = UserGroup.UserGroupBuilder.userGroup().withUserGroupKey(new UserGroupKey(member.getId(), saved.getId()))
                .withUser(userRepository.findById(member.getId()).orElse(null)).withGroup(groupRepository.findById(saved.getId()).orElse(null))
                .withIsHost(isHost).build();

            userGroupRepository.save(newMember);
        }

        return new GroupCreateDto(saved.getId(), saved.getName(), toCreate.getHost(), toCreate.getCocktails(),
            toCreate.getMembers()); // todo: return created group
    }

    @Override
    public GroupCreateDto update(GroupCreateDto toUpdate) throws NotFoundException, ValidationException, ConflictException {
        LOGGER.trace("update({})", toUpdate);
        validator.validateForUpdate(toUpdate);
        // todo update group in database
        return null; // todo return updated group
    }

    @Override
    @Transactional
    public List<UserGroup> findGroupsByUser(String email) {
        LOGGER.trace("findGroupsByUser({})", email);
        // find groups in database
        List<UserGroup> groups = userGroupRepository.findAllByApplicationUser(userRepository.findByEmail(email));
        return groups;
    }

    @Override
    public void makeMemberHost(Long groupId, Long userId, String currentUserMail) throws ValidationException {
        LOGGER.trace("makeMemberHost({}, {}, {})", groupId, userId, currentUserMail);

        // check if group exists
        ApplicationGroup group = groupRepository.findById(groupId).orElse(null);
        if (group == null) {
            throw new NotFoundException("Could not find group");
        }

        // check if current user is host of the group
        ApplicationUser currentUser = userRepository.findByEmail(currentUserMail);
        if (currentUser == null) {
            throw new NotFoundException("Could not find current user");
        }
        UserGroup currentUserGroup = userGroupRepository.findById(new UserGroupKey(currentUser.getId(), groupId)).orElse(null);
        if (currentUserGroup == null || !currentUserGroup.isHost()) {
            throw new ValidationException("You are not allowed to make this user host", List.of("You are not the host of this group"));
        }

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
}
