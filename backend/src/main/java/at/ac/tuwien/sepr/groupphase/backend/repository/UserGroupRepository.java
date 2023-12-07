package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationGroup;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.UserGroup;
import at.ac.tuwien.sepr.groupphase.backend.entity.UserGroupKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository of UserGroup entity.
 */
@Repository
public interface UserGroupRepository extends JpaRepository<UserGroup, UserGroupKey> {
    List<UserGroup> findAllByApplicationUser(ApplicationUser applicationUser);

    List<UserGroup> findAllByApplicationGroup(ApplicationGroup applicationGroup);

    @Query("SELECT u.applicationUser FROM UserGroup u WHERE u.id.group = :groupId")
    List<ApplicationUser> findUsersByGroupId(@Param("groupId")Long groupId);

    List<ApplicationUser> findByApplicationGroup(ApplicationGroup applicationGroup);

}
