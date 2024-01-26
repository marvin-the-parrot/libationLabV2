package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationMessage;

import java.util.List;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository of Message entity.
 */
@Repository
public interface MessageRepository extends JpaRepository<ApplicationMessage, Long> {

    /**
     * Find all message entries ordered by published at date (descending).
     *
     * @param user   of message
     * @return ordered list of al message entries
     */
    List<ApplicationMessage> findAllByApplicationUserOrderByIsReadAscSentAtDesc(ApplicationUser user);

    /**
     * Find count of all message entries of a user with isRead false.
     *
     * @param user   of message
     * @param isRead of message
     * @return ordered list of al message entries
     */
    long countByApplicationUserAndIsRead(ApplicationUser user, boolean isRead);

    /**
     * Find all message entries of a group and user.
     *
     * @param user  of message
     * @param groupId of message
     * @return list of all message entries
     */
    List<ApplicationMessage> findAllByApplicationUserAndGroupId(ApplicationUser user, Long groupId);

    /**
     * Find all message entries of a given id list.
     *
     * @param ids of message
     * @return list of all message entries
     */
    List<ApplicationMessage> findByIdIn(List<Long> ids);
}
