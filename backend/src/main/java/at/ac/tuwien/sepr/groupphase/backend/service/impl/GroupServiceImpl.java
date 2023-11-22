package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.ac.tuwien.sepr.groupphase.backend.entity.Group;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.GroupService;
import at.ac.tuwien.sepr.groupphase.backend.entity.Member;
import at.ac.tuwien.sepr.groupphase.backend.repository.MemberRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.Optional;
import java.util.List;
import java.util.Set;

import io.jsonwebtoken.lang.Arrays;

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
    public Group findOne(Long id) {
        LOGGER.debug("Find group with id {}", id);
        Optional<Group> group = groupRepository.findById(id);
        if (group.isPresent()) {
            return group.get();
        } else {
            throw new NotFoundException(String.format("Could not find group with id %s", id));
        }
    }
    

	@Override
	public Boolean deleteGroup(Long groupId, Long hostId) {
		Optional<Member> host = memberRepository.findById(hostId);
		
		if(groupRepository.findById(groupId).isEmpty() && isHostExists(host)) {
			return false;
		}
				
		groupRepository.deleteById(groupId);
		return true;
	}

	@Override
	public Boolean deleteMember(Long groupId, Long hostId, Long memberId) {
		Optional<Member> host = memberRepository.findById(hostId);
		Optional<Member> memberToDelete = memberRepository.findById(hostId);
		Optional<Group> group = groupRepository.findById(groupId);
		Set<Member> members = group.get().getMembers();

		if(group.isEmpty() && isHostExists(host) && memberToDelete.isEmpty() && !members.contains(memberToDelete)) {
			return false;
		}
		//TODO
	//	groupRepository. delete member of group
		
		return true;
	}

	@Override
	public List<Member> searchForMember(Long groupId, String memberName) {
		Optional<Group> group = groupRepository.findById(groupId);
		if (group.isEmpty()) {
			return List.of();
		}		
		return groupRepository.searchForMembers(groupId, memberName);
	}
	
	
	private boolean isHostExists(Optional<Member> host) {
		return host.isEmpty() || !host.get().isHost();
	}
}
