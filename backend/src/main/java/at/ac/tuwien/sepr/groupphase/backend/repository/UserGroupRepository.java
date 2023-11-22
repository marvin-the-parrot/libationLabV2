package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.UserGroup;
import at.ac.tuwien.sepr.groupphase.backend.entity.UserGroupKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository of UserGroup entity.
 *
 */
@Repository
public interface UserGroupRepository extends JpaRepository<UserGroup,  UserGroupKey> {

  @Modifying
  @Query("DELETE FROM UserGroup ug WHERE ug.id.user.id = :userId AND ug.id.group.id = :groupId")
  void deleteByUserIdAndGroupId(@Param("userId") Long userId, @Param("groupId") Long groupId);
    
}
