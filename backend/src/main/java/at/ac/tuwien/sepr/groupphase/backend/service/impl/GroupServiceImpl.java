package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationGroup;
import at.ac.tuwien.sepr.groupphase.backend.entity.UserGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.GroupService;
import at.ac.tuwien.sepr.groupphase.backend.repository.MemberRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.Optional;
import java.util.List;

@Service
public class GroupServiceImpl implements GroupService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private MemberRepository memberRepository;

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
    public Boolean deleteGroup(Long groupId, Long hostId) {
        Optional<UserGroup> host = memberRepository.findById(hostId);

        if (groupRepository.findById(groupId).isEmpty() && isHostExists(host)) {
            return false;
        }

        groupRepository.deleteById(groupId);
        return true;
    }

    @Override
    public Boolean deleteMember(Long groupId, Long hostId, Long memberId) {
        Optional<UserGroup> host = memberRepository.findById(hostId);
        Optional<UserGroup> memberToDelete = memberRepository.findById(hostId);
        Optional<ApplicationGroup> group = groupRepository.findById(groupId);
        //Set<UserGroup> userGroups = group.get().getMembers();

        // TODO uncomment
        //if(group.isEmpty() && isHostExists(host) && memberToDelete.isEmpty() && !userGroups.contains(memberToDelete)) {
        //return false;
        //}

        //groupRepository. delete member of group

        return true;
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
        return host.isEmpty() || !host.get().isHost();
    }
}
