package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationGroup;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Cocktail;
import at.ac.tuwien.sepr.groupphase.backend.entity.Feedback;
import at.ac.tuwien.sepr.groupphase.backend.entity.FeedbackKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * Repository of Feedback entity.
 */
@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    Feedback findByApplicationUserAndApplicationGroupAndCocktail(ApplicationUser user, ApplicationGroup group, Cocktail cocktail);

    List<Feedback> findByApplicationUserAndApplicationGroupAndCocktailIn(ApplicationUser user, ApplicationGroup group, Set<Cocktail> cocktails);

    List<Feedback> findByApplicationGroupAndCocktailIn(ApplicationGroup group, Set<Cocktail> cocktails);

    List<Feedback> findByApplicationGroup(ApplicationGroup group);

    List<Feedback> findByApplicationUserAndApplicationGroup(ApplicationUser user, ApplicationGroup group);

    Feedback findByFeedbackKey(FeedbackKey feedbackKey);
}
