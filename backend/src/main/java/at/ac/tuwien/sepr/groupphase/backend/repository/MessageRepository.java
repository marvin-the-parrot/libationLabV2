package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationMessage;

import java.util.List;

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
     * @return ordered list of al message entries
     */
    List<ApplicationMessage> findAllByOrderBySentAtDesc();
}
