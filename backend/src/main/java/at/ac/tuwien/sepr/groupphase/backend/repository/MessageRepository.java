package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationMessage;

import java.util.List;
import java.util.Optional;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository of Message entity.
 */
@Repository
public interface MessageRepository extends JpaRepository<ApplicationMessage, Long> {

    /**
     * Find all message entries ordered by published at date (descending).
     *
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
}
