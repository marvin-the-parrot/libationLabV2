package at.ac.tuwien.sepr.groupphase.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Preference;

/**
 * Repository of Preference entity.
 */
@Repository
public interface PreferenceRepository extends JpaRepository<Preference, Long> {

}
