package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.GroupCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.GroupOverviewDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserListDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationGroup;
import at.ac.tuwien.sepr.groupphase.backend.entity.UserGroup;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;

import java.util.List;

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
     */
    void deleteGroup(Long groupId) throws ValidationException;

    /**
     * Deleting member user in group, only possible by host.
     *
     * @param groupId the id of the group
     * @param userId  the id of member to be deleted
     */
    void deleteMember(Long groupId, Long userId) throws ValidationException;

    /**
     * Searching for member of group.
     *
     * @param groupId the id of the group
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
    GroupCreateDto update(GroupCreateDto toUpdate) throws NotFoundException, ValidationException, ConflictException;

    /**
     * Find all group entries for this user.
     *
     * @return all group entries for this user
     */
    List<UserGroup> findGroupsByUser();

    /**
     * Make the user with the given ID host of the group with the given ID.
     *
     * @param groupId the ID of the group
     * @param userId  the ID of the user to make host
     * @throws ValidationException if the user is not a member of the group or the current user is not the host
     */
    void makeMemberHost(Long groupId, Long userId) throws ValidationException;


    /**
     * Returns the requested group.
     *
     * @param id the id of the group
     * @return the requested group as GroupOverviewDto
     * @throws NotFoundException   if the group does not exist
     * @throws ValidationException if the user is not a member of the group
     */
    GroupOverviewDto findGroupById(Long id) throws NotFoundException, ValidationException;
}
