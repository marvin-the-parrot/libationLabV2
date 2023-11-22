package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationGroup;
import at.ac.tuwien.sepr.groupphase.backend.entity.UserGroup;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.MemberRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserGroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.GroupService;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Group service implementation.
 *
 */
@Service
public class GroupServiceImpl implements GroupService {

  private static final Logger LOGGER 
      = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @Autowired
  private GroupRepository groupRepository;

  @Autowired
  private MemberRepository memberRepository;

  @Autowired
  private UserGroupRepository userGroupRepository;
  
  public GroupServiceImpl(GroupRepository groupRepository) {
    this.groupRepository = groupRepository;
  }

  @Override
  public ApplicationGroup findOne(Long id) {
    LOGGER.debug("Find group with id {}", id);
    Optional<ApplicationGroup> group = groupRepository.findById(id);
    if (group.isPresent()) {
      return group.get();
    } else {
      throw new NotFoundException(String.format("Could not find group with id %s", id));
    }
  }


  @Override
  public void deleteGroup(Long groupId, Long hostId) {
    Optional<UserGroup> host = memberRepository.findById(hostId);
    if (isHostExists(host)) {
      groupRepository.deleteById(groupId);
    } else {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }
  }

  @Override
  public void deleteMember(Long groupId, Long hostId, Long memberId) {
    ApplicationGroup group = groupRepository.findById(groupId).orElse(null);
    Optional<UserGroup> host = memberRepository.findById(hostId);
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
    userGroupRepository.deleteByUserIdAndGroupId(memberId, groupId);
  }

  @Override
  public List<UserGroup> searchForMember(Long groupId, String memberName) {
        // TODO uncomment
        /*Optional<Group> group = groupRepository.findById(groupId);
        if (group.isEmpty()) {
        return List.of();
        }
        return groupRepository.searchForMembers(groupId, memberName);*/
    return null;
  }


  private boolean isHostExists(Optional<UserGroup> host) {
    return !host.isEmpty() || host.get().isHost();
  }
}
