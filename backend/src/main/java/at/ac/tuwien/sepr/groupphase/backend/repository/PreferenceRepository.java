package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredient;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Preference;

import java.util.List;
import java.util.Set;

/**
 * Repository of Preference entity.
 */
@Repository
@Transactional
public interface PreferenceRepository extends JpaRepository<Preference, Long> {

    List<Preference> findAllByApplicationUser(ApplicationUser applicationUser);

    List<Preference> findAllByApplicationUserInOrderByName(List<ApplicationUser> applicationUser);

    List<Preference> findFirst10ByNameIgnoreCaseContainingOrderByName(String preferenceName);

    List<Preference> findFirst10ByNameNotInAndNameIgnoreCaseContainingOrderByName(List<String> names, String preferenceName);

    List<Preference> findByNameEqualsIgnoreCase(String name);

    List<Preference> findAllByApplicationUserIsIn(List<ApplicationUser> applicationUsers);

}
