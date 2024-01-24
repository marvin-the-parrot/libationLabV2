package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CocktailFeedbackDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CocktailFeedbackHostDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.FeedbackCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.FeedbackState;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.FeedbackMapper;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.HashMap;
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
    private final FeedbackMapper feedbackMapper;

    @Autowired
    public FeedbackServiceImpl(FeedbackRepository feedbackRepository, UserRepository userRepository, CocktailRepository cocktailRepository, GroupRepository groupRepository, UserGroupRepository userGroupRepository, FeedbackMapper feedbackMapper) {
        this.feedbackRepository = feedbackRepository;
        this.userRepository = userRepository;
        this.cocktailRepository = cocktailRepository;
        this.groupRepository = groupRepository;
        this.userGroupRepository = userGroupRepository;
        this.feedbackMapper = feedbackMapper;
    }

    @Override
    public List<CocktailFeedbackHostDto> getRatings(Long groupId) throws NotFoundException {
        LOGGER.debug("Get ratings for group {}", groupId);

        List<Feedback> feedbacks = feedbackRepository.findByApplicationGroup(groupRepository.findById(groupId).orElseThrow(() -> new NotFoundException("Group not found")));

        int[] ratings = new int[2];
        HashMap<Cocktail, int[]> cocktailRatings = new HashMap<>();
        List<Cocktail> cocktails = cocktailRepository.findDistinctByFeedbacksIn(feedbacks);
        cocktailRatings.put(cocktails.get(0), ratings);

        int index = 0;
        for (Feedback feedback : feedbacks) {
            if (feedback.getCocktail() == cocktailRatings.keySet().toArray()[index]) {
                if (feedback.getRating() == FeedbackState.Like) {
                    ratings[0]++;
                } else if (feedback.getRating() == FeedbackState.Dislike) {
                    ratings[1]++;
                }

                cocktailRatings.replace(cocktails.get(index), ratings);
            } else {
                ratings = new int[2];
                if (feedback.getRating() == FeedbackState.Like) {
                    ratings[0]++;
                } else if (feedback.getRating() == FeedbackState.Dislike) {
                    ratings[1]++;
                }
                cocktailRatings.put(feedback.getCocktail(), ratings);

                index++;
            }
        }

        return feedbackMapper.cocktailFeedbackToCocktailFeedbackHostDto(cocktailRatings);
    }

    @Transactional
    @Override
    public void create(FeedbackCreateDto feedbackToCreate) throws NotFoundException {
        LOGGER.debug("Create recommendation {}", feedbackToCreate);

        //TODO validation

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
    public void update(CocktailFeedbackDto feedbackToUpdate) throws NotFoundException {
        LOGGER.debug("Update recommendation {}", feedbackToUpdate);

        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        ApplicationUser user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new NotFoundException("User not found");
        }

        ApplicationGroup group = groupRepository.findById(feedbackToUpdate.getGroupId()).orElseThrow(() -> new NotFoundException("Group not found"));

        Cocktail cocktails = cocktailRepository.findById(feedbackToUpdate.getCocktailId()).orElseThrow(() -> new NotFoundException("Cocktail not found"));

        Feedback feedback = feedbackRepository.findByApplicationUserAndApplicationGroupAndCocktail(user, group, cocktails);
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
