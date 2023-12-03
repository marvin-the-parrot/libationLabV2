package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Optional;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.GroupCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.GroupOverviewDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.UserGroupKey;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationGroup;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.UserGroup;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserGroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.GroupService;
import at.ac.tuwien.sepr.groupphase.backend.service.validators.GroupValidator;

/**
 * Group service implementation.
 */
@Service
public class GroupServiceImpl implements GroupService {

    private static final Logger LOGGER
        = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final UserService userService;
    @Autowired
    private final GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserGroupRepository userGroupRepository;

    private final GroupValidator validator;

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
    public void deleteGroup(Long groupId, Long hostId) {
        LOGGER.debug("Delete group by host with group id {}", groupId, hostId);
        Optional<ApplicationUser> host = userRepository.findById(hostId);
        if (isHostExists(host)) {
            groupRepository.deleteById(groupId);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @SuppressWarnings("unlikely-arg-type")
    @Override
    public void deleteMember(Long groupId, Long hostId, Long memberId) {
        LOGGER.debug("Delete group member by host with group and member id {}",
            groupId, hostId, memberId);
        ApplicationGroup group = groupRepository.findById(groupId).orElse(null);
        Optional<ApplicationUser> host = userRepository.findById(hostId);
        if (!isHostExists(host)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        if (group != null) {
            UserGroup userToRemove = group.getMembers().stream()
                .filter(user -> user.getUser().getId().equals(memberId))
                .findFirst()
                .orElse(null);

            if (userToRemove != null) {
                group.getMembers().remove(userToRemove.getUser());
                groupRepository.save(group);
            }
        }
        userGroupRepository.deleteById(new UserGroupKey(memberId, groupId));
    }

    @Override
    public Optional<ApplicationUser> searchForMember(Long groupId, String memberName) {
        LOGGER.debug("Search for member in group, by member name and group id {}", groupId, memberName);
        return groupRepository.searchForMembers(groupId, memberName);
    }

    @Override
    public GroupCreateDto create(GroupCreateDto toCreate) throws ValidationException, ConflictException {
        LOGGER.trace("create({})", toCreate);
        // validate group:
        validator.validateForCreate(toCreate);

        // todo: save members in database
        // todo: save host in database
        // build group entity and save it:
        ApplicationGroup group = ApplicationGroup.GroupBuilder.group()
            .withName(toCreate.getName())
            .build();
        LOGGER.debug("saving group {}", group);
        ApplicationGroup saved = groupRepository.save(group);
        return new GroupCreateDto(saved.getId(), saved.getName(), null, null, null);
    }

    @Override
    public GroupCreateDto update(GroupCreateDto toUpdate)
        throws NotFoundException, ValidationException, ConflictException {
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

    private boolean isHostExists(Optional<ApplicationUser> host) {
        return !host.isEmpty() && host.get().getAdmin();
    }
}
