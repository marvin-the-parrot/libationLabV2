package at.ac.tuwien.sepr.groupphase.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.UserGroup;
import at.ac.tuwien.sepr.groupphase.backend.entity.UserGroupKey;

/**
 * Repository of UserGroup entity.
 */
@Repository
public interface UserGroupRepository extends JpaRepository<UserGroup, UserGroupKey> {

    List<UserGroup> findByApplicationGroup_Id(Long groupId);
}
