package at.ac.tuwien.sepr.groupphase.backend.service;


import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserGroupDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.UserGroup;

public interface UserGroupService {

    /**
     * Save a single userGroup entry.
     *
     */
    void create(Long groupId);

}
