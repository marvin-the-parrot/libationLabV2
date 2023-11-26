package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//TODO: replace this class with a correct ApplicationUser JPARepository implementation

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

    ApplicationUser findById(Integer id);

}
