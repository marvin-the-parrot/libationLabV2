package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MessageCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MessageDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationMessage;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import org.hibernate.exception.ConstraintViolationException;

import java.util.List;

/**
 * Service for Message Entity.
 */
public interface MessageService {

    /**
     * Get all unread messages for the current user.
     *
     * @return number of unread messages
     */
    long getUnreadMessageCount() throws NotFoundException;

    /**
     * Find all message entries ordered by published at date (descending).
     *
     * @return ordered list of al message entries
     */
    List<ApplicationMessage> findAll() throws NotFoundException;

    /**
     * Save a single message entry.
     *
     * @param message to publish
     * @return saved message entry
     */
    ApplicationMessage create(MessageCreateDto message) throws ConstraintViolationException, ValidationException;

    /**
     * Updates the message with given ID with the data given in {@code toUpdate}.
     *
     * @param toUpdate the data of the message to update
     * @return the updated message
     * @throws NotFoundException   if the message with given ID does not exist
     *                             in the persistent data store
     * @throws ValidationException if the data given for the message
     *                             is in itself incorrect (no name, name too long …)
     * @throws ConflictException   if the data given for the message
     *                             is in conflict the data currently in the system
     */
    ApplicationMessage update(MessageDetailDto toUpdate)
        throws NotFoundException, ValidationException, ConflictException;

    /**
     * Deleting message entry by id.
     *
     * @param messageId the id of the message
     */
    void delete(Long messageId) throws NotFoundException;
}
