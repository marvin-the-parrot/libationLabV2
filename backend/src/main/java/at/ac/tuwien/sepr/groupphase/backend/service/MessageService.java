package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationMessage;

import java.util.List;

/**
 * Service for Message Entity.
 */
public interface MessageService {

    /**
     * Find all message entries ordered by published at date (descending).
     *
     * @return ordered list of al message entries
     */
    List<ApplicationMessage> findAll();

    ApplicationMessage findById(Long id);

    /**
     * Publish a single message entry.
     *
     * @param applicationMessage to publish
     * @return published message entry
     */
    ApplicationMessage publishMessage(ApplicationMessage applicationMessage);

}
