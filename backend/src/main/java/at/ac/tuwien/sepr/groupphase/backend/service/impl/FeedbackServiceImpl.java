package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CocktailFeedbackDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.FeedbackCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationGroup;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Cocktail;
import at.ac.tuwien.sepr.groupphase.backend.entity.Feedback;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.CocktailRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.FeedbackRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.FeedbackService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public FeedbackServiceImpl(FeedbackRepository feedbackRepository, UserRepository userRepository, CocktailRepository cocktailRepository, GroupRepository groupRepository) {
        this.feedbackRepository = feedbackRepository;
        this.userRepository = userRepository;
        this.cocktailRepository = cocktailRepository;
        this.groupRepository = groupRepository;
    }

    @Override
    public void create(FeedbackCreateDto feedbackToCreate) throws NotFoundException {
        LOGGER.debug("Create recommendation {}", feedbackToCreate);

        //TODO validation

        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        ApplicationUser user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new NotFoundException("User not found");
        }

        ApplicationGroup group = groupRepository.findById(feedbackToCreate.getGroupId()).orElseThrow(() -> new NotFoundException("Group not found"));

        Set<Cocktail> cocktails = cocktailRepository.findByIdIn(List.of(feedbackToCreate.getCocktailIds()));
        for (Cocktail cocktail : cocktails) {
            Feedback feedback = new Feedback();

            feedback.setApplicationUser(user);
            feedback.setApplicationGroup(group);
            feedback.setCocktail(cocktail);

            feedbackRepository.save(feedback);
        }
    }

    @Override
    public void update(CocktailFeedbackDto feedbackToUpdate) throws NotFoundException {
        LOGGER.debug("Update recommendation {}", feedbackToUpdate);

        //TODO validation

        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        ApplicationUser user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new NotFoundException("User not found");
        }
    }
}
