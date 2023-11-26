package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository of ApplicationUser entity.
 */
@Repository
public interface ApplicationUserRepository extends JpaRepository<ApplicationUser, Long> {

}

