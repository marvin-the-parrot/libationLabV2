package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


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
     * Find user by name.
     *
     * @param name of user
     * @return ApplicationUser found by name
     */
    ApplicationUser findByName(String name);

    /**
     * Find users by username for adding Users to existing Group.
     *
     * @param username of user
     * @return List of ApplicationUser found by username
     */
    List<ApplicationUser> findFirst5ByEmailNotAndEmailNotInAndNameIgnoreCaseContaining(String email, List<String> emails, String username);

    /**
     * Find users by username for adding Users at creating Group.
     *
     * @param username of user
     * @return List of ApplicationUser found by username
     */
    List<ApplicationUser> findFirst5ByEmailNotAndNameIgnoreCaseContaining(String email, String username);

    ApplicationUser findApplicationUsersByUserGroups(UserGroup userGroups);

}
