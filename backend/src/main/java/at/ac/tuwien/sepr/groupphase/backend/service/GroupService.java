package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.GroupDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationGroup;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;

import java.util.Optional;


/**
 * Service for ApplicationGroup Entity.
 */
public interface GroupService {

    /**
     * Find a single group entry by id.
     *
     * @param id the id of the group entry
     * @return the group entry
     */
    ApplicationGroup findOne(Long id);

    /**
     * Deleting group entry by id, only possible by host.
     *
     * @param groupId the id of the group
     * @param hostId  the id of the host
     */
    void deleteGroup(Long groupId, Long hostId);

    /**
     * Deleting member user in group, only possible by host.
     *
     * @param groupId  the id of the group
     * @param hostId   the id of the host
     * @param memberId the id of member to be deleted
     */
    void deleteMember(Long groupId, Long hostId, Long memberId);

    /**
     * Searching for member of group.
     *
     * @param groupId    the id of the group
     * @param memberName the id of the member of group
     */
    Optional<ApplicationUser> searchForMember(Long groupId, String memberName);

    /**
     * Creates a new group with the data given in {@code toCreate}.
     *
     * @param toCreate the data of the group to create
     * @return the created group
     * @throws ValidationException if the data given for the group is in itself incorrect (no name, name too long …)
     * @throws ConflictException  if the data given for the group is in conflict the data currently in the system
     */
    GroupDetailDto create(GroupDetailDto toCreate) throws ValidationException, ConflictException;
}
