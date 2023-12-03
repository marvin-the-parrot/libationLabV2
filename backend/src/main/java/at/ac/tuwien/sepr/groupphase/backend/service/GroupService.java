package at.ac.tuwien.sepr.groupphase.backend.service;

import java.util.List;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.GroupCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserListDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationGroup;
import at.ac.tuwien.sepr.groupphase.backend.entity.UserGroup;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;

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
    ApplicationGroup findOne(Long id) throws NotFoundException;

    /**
     * Deleting group entry by id, only possible by host.
     *
     * @param groupId the id of the group
     * @param currentUserMail the mail of the user that sends the request
     */
    void deleteGroup(Long groupId, String currentUserMail) throws ValidationException;

    /**
     * Deleting member user in group, only possible by host.
     *
     * @param groupId  the id of the group
     * @param userId the id of member to be deleted
     * @param currentUserMail the email of the user that sends the request
     */
    void deleteMember(Long groupId, Long userId, String currentUserMail) throws ValidationException;

    /**
     * Searching for member of group.
     *
     * @param groupId    the id of the group
     */
    List<UserListDto> searchForMember(Long groupId);

    /**
     * Creates a new group with the data given in {@code toCreate}.
     *
     * @param toCreate the data of the group to create
     * @return the created group
     * @throws ValidationException if the data given for the group
     *                             is in itself incorrect (no name, name too long …)
     * @throws ConflictException   if the data given for the group
     *                             is in conflict the data currently in the system
     */
    GroupCreateDto create(GroupCreateDto toCreate) throws ValidationException, ConflictException;

    /**
     * Updates the group with given ID with the data given in {@code toUpdate}.
     *
     * @param toUpdate the data of the group to update
     * @return the updated group
     * @throws NotFoundException   if the group with given ID does not exist
     *                             in the persistent data store
     * @throws ValidationException if the data given for the group
     *                             is in itself incorrect (no name, name too long …)
     * @throws ConflictException   if the data given for the group
     *                             is in conflict the data currently in the system
     */
    GroupCreateDto update(GroupCreateDto toUpdate)
        throws NotFoundException, ValidationException, ConflictException;

    /**
     * Find all group entries for this user.
     *
     * @param email the email of the user, used as an identifier
     * @return all group entries for this user
     */
    List<UserGroup> findGroupsByUser(String email);
}
