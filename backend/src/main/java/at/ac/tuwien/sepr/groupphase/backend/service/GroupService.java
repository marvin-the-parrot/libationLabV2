package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.entity.Group;

public interface GroupService {

    /**
     * Find a single group entry by id.
     *
     * @param id the id of the group entry
     * @return the group entry
     */
        Group findOne(Long id);
}
