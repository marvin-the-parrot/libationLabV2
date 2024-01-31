package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.ResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository of ResetToken entity.
 */
public interface ResetTokenRepository extends JpaRepository<ResetToken, Long> {

    ResetToken findByToken(String token);
}
