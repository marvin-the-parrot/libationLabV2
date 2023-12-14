package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredient;
import at.ac.tuwien.sepr.groupphase.backend.repository.IngredientsRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Set;

@Profile("generateData")
@Component
public class IngredientsDataGenerator {

    private static final Logger LOGGER =
        LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int NUMBER_OF_INGREDIENTS_TO_GENERATE = 10;
    private final IngredientsRepository ingredientsRepository;
    private final UserRepository userRepository;

    public IngredientsDataGenerator(IngredientsRepository ingredientsRepository, UserRepository userRepository) {
        this.ingredientsRepository = ingredientsRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    @PostConstruct
    public void generateIngredients() {
        if (ingredientsRepository.findAll().size() > 0) {
            LOGGER.debug("ingredients already generated");
        } else {
            LOGGER.debug("generating {} ingredients entries", NUMBER_OF_INGREDIENTS_TO_GENERATE);
            for (int i = 0; i < NUMBER_OF_INGREDIENTS_TO_GENERATE; i++) {
                Ingredient ingredient = Ingredient.IngredientsBuilder.ingredients()
                    .withId((long) i)
                    .withName("Ingredient" + i)
                    .build();
                ingredientsRepository.save(ingredient);
            }
            LOGGER.debug("generating {} ingredients entries", NUMBER_OF_INGREDIENTS_TO_GENERATE);

            List<ApplicationUser> users = userRepository.findAll();
            List<Ingredient> ingredients = ingredientsRepository.findAll();

            for (ApplicationUser user : users) {
                Set<Ingredient> ingredientSet = Set.copyOf(ingredients);
                user.setIngredients(ingredientSet);
                userRepository.save(user);
                ingredients.remove(0);
            }
        }
    }
}
