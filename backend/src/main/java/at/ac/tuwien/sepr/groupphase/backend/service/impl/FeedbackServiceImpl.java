package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CocktailFeedbackDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.FeedbackCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.FeedbackState;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationGroup;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Cocktail;
import at.ac.tuwien.sepr.groupphase.backend.entity.Feedback;
import at.ac.tuwien.sepr.groupphase.backend.entity.FeedbackKey;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.CocktailRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.FeedbackRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserGroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.FeedbackService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.InvalidEndpointRequestException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Set;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    private static final Logger LOGGER =
        LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;
    private final CocktailRepository cocktailRepository;
    private final GroupRepository groupRepository;
    private final UserGroupRepository userGroupRepository;

    @Autowired
    public FeedbackServiceImpl(FeedbackRepository feedbackRepository, UserRepository userRepository, CocktailRepository cocktailRepository, GroupRepository groupRepository, UserGroupRepository userGroupRepository) {
        this.feedbackRepository = feedbackRepository;
        this.userRepository = userRepository;
        this.cocktailRepository = cocktailRepository;
        this.groupRepository = groupRepository;
        this.userGroupRepository = userGroupRepository;
    }

    @Transactional
    @Override
    public void createFeedbackRelations(FeedbackCreateDto feedbackToCreate) throws InvalidEndpointRequestException, NotFoundException {
        LOGGER.debug("Create feedback {}", feedbackToCreate);

        if (feedbackToCreate.getGroupId() == null || feedbackToCreate.getCocktailIds() == null) {
            throw new InvalidEndpointRequestException("No Feedback to create sent", "the sent groupId or cocktailIds are null");
        }

        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        ApplicationUser host = userRepository.findByEmail(userEmail);
        if (host == null) {
            throw new NotFoundException("User not found");
        }

        ApplicationGroup group = groupRepository.findById(feedbackToCreate.getGroupId()).orElseThrow(() -> new NotFoundException("Group not found"));
        List<ApplicationUser> users = userGroupRepository.findUsersByGroupId(group.getId());

        Set<Cocktail> cocktails = cocktailRepository.findByIdIn(List.of(feedbackToCreate.getCocktailIds()));
        for (Cocktail cocktail : cocktails) {
            for (ApplicationUser user : users) {
                Feedback feedback = new Feedback();

                feedback.setFeedbackKey(new FeedbackKey(user.getId(), group.getId(), cocktail.getId()));
                feedback.setApplicationUser(user);
                feedback.setApplicationGroup(group);
                feedback.setCocktail(cocktail);
                feedback.setRating(FeedbackState.NotVoted);

                feedbackRepository.save(feedback);
            }
        }

        deleteFeedbackRelationsAtCocktailChange(group, cocktails);
    }

    @Transactional
    @Override
    public void createFeedbackRelationsForNewUser(ApplicationGroup group, ApplicationUser user) throws NotFoundException {
        LOGGER.debug("Create feedback relations for new user {}", user.getName());

        for (Cocktail cocktail : group.getCocktails()) {
            Feedback feedback = new Feedback();

            feedback.setFeedbackKey(new FeedbackKey(user.getId(), group.getId(), cocktail.getId()));
            feedback.setApplicationUser(user);
            feedback.setApplicationGroup(group);
            feedback.setCocktail(cocktail);
            feedback.setRating(FeedbackState.NotVoted);

            feedbackRepository.save(feedback);
        }
    }

    @Transactional
    @Override
    public void updateRatings(CocktailFeedbackDto feedbackToUpdate) throws NotFoundException {
        LOGGER.debug("Update feedback {}", feedbackToUpdate);

        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        ApplicationUser user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new NotFoundException("User not found");
        }

        ApplicationGroup group = groupRepository.findById(feedbackToUpdate.getGroupId()).orElseThrow(() -> new NotFoundException("Group not found"));

        Cocktail cocktail = cocktailRepository.findById(feedbackToUpdate.getCocktailId()).orElseThrow(() -> new NotFoundException("Cocktail not found"));

        Feedback feedback = feedbackRepository.findByApplicationUserAndApplicationGroupAndCocktail(user, group, cocktail);
        if (feedback == null) {
            throw new NotFoundException("Feedback not found");
        }

        feedback.setRating(feedbackToUpdate.getRating());
        feedbackRepository.save(feedback);
    }

    @Override
    public void deleteFeedbackRelationsAtCocktailChange(Long groupId, Long userId) throws NotFoundException {
        ApplicationGroup group = groupRepository.findById(groupId).orElseThrow(() -> new NotFoundException("Group not found"));
        ApplicationUser user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));

        List<Feedback> feedbacks = feedbackRepository.findByApplicationUserAndApplicationGroup(user, group);

        if (feedbacks.isEmpty()) {
            return;
        }

        feedbackRepository.deleteAll(feedbacks);
    }

    private void deleteFeedbackRelationsAtCocktailChange(ApplicationGroup group, Set<Cocktail> cocktails) throws NotFoundException {
        LOGGER.debug("delete feedback {}", cocktails);

        List<Feedback> newFeedbacks = feedbackRepository.findByApplicationGroupAndCocktailIn(group, cocktails);
        List<Feedback> oldFeedbacks = feedbackRepository.findByApplicationGroup(group);

        for (Feedback oldFeedback : oldFeedbacks) {
            if (!newFeedbacks.contains(oldFeedback)) {
                feedbackRepository.delete(oldFeedback);
            }
        }
    }
}
