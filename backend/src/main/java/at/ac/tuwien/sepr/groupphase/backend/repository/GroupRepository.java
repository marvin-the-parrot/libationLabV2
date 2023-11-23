package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationGroup;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import jakarta.transaction.Transactional;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


/**
 * Repository of Group entity.
 */
@Repository
@Transactional
public interface GroupRepository extends
    JpaRepository<ApplicationGroup, Long> {

    @Query("SELECT ug.applicationUser FROM UserGroup ug "
        + "WHERE ug.applicationGroup.id = :groupId AND ug.applicationGroup.name LIKE %:partialName%")
    Optional<ApplicationUser> searchForMembers(@Param("groupId") Long groupId,
                                               @Param("partialName") String memberName);

}
