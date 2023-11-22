package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.entity.Group;

import java.util.List;

import at.ac.tuwien.sepr.groupphase.backend.entity.UserGroup;

public interface GroupService {

    /**
     * Find a single group entry by id.
     *
     * @param id the id of the group entry
     * @return the group entry
     */
        Group findOne(Long id);

        Boolean deleteGroup(Long groupId, Long hostId);

        Boolean deleteMember(Long groupId, Long hostId, Long memberId);

        List<UserGroup> searchForMember(Long groupId, String memberName);
}
