package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;


/**
 * Repository of ApplicationUser entity.
 */
@Repository
public interface UserRepository extends JpaRepository<ApplicationUser, Long> {

    /**
     * Find user by email.
     *
     * @param email of user
     * @return ApplicationUser found by email
     */
    ApplicationUser findByEmail(String email);

    /**
     * Find users by username.
     *
     * @param username of user
     * @return List of ApplicationUser found by username
     */
    List<ApplicationUser> findFirst5ByNameIgnoreCaseContaining(String username);

    ApplicationUser findApplicationUsersByUserGroups(UserGroup userGroups);

}
