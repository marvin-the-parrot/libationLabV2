package at.ac.tuwien.sepr.groupphase.backend.service;


import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserGroupDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.UserGroup;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import org.hibernate.exception.ConstraintViolationException;

public interface UserGroupService {

    /**
     * Save a single userGroup entry.
     *
     */
    void create(Long groupId) throws ConstraintViolationException;

}
