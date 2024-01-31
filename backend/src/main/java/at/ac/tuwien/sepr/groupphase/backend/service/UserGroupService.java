package at.ac.tuwien.sepr.groupphase.backend.service;

import org.hibernate.exception.ConstraintViolationException;

public interface UserGroupService {

    /**
     * Save a single userGroup entry.
     */
    void create(Long groupId) throws ConstraintViolationException;

}
