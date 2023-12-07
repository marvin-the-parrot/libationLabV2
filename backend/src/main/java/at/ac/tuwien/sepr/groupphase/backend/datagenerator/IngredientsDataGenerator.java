package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredient;
import at.ac.tuwien.sepr.groupphase.backend.repository.IngredientsRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import jakarta.annotation.PostConstruct;
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

    //TODO change name to IngredientsDataGenerator
    //the profile is started in alphabetical order and therefore there are no ingredients
    //available to set for the users in the many to many relationship
    private static final Logger LOGGER =
        LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int NUMBER_OF_INGREDIENTS_TO_GENERATE = 10;
    private final IngredientsRepository ingredientsRepository;
    private final UserRepository userRepository;

    public IngredientsDataGenerator(IngredientsRepository ingredientsRepository, UserRepository userRepository) {
        this.ingredientsRepository = ingredientsRepository;
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void generateIngredients() {
        if (ingredientsRepository.findAll().size() > 0) {
            LOGGER.debug("ingredients already generated");
        } else {
            LOGGER.debug("generating {} ingredients entries", NUMBER_OF_INGREDIENTS_TO_GENERATE);
            List<ApplicationUser> users = userRepository.findAll();
            for (int i = 0; i < NUMBER_OF_INGREDIENTS_TO_GENERATE; i++) {
                Ingredient ingredient = Ingredient.IngredientsBuilder.ingredients()
                    .withId((long) i)
                    .withName("Ingredient" + i)
                    .build();
                users.get(1).setIngredients(Set.copyOf(List.of(ingredient)));
                if (i > 3) {
                    users.get(2).setIngredients(Set.copyOf(List.of(ingredient)));
                }
                if (i > 5) {
                    users.get(3).setIngredients(Set.copyOf(List.of(ingredient)));
                }
                if (i > 7) {
                    users.get(4).setIngredients(Set.copyOf(List.of(ingredient)));
                }
                if (i > 8) {
                    users.get(5).setIngredients(Set.copyOf(List.of(ingredient)));
                    users.get(6).setIngredients(Set.copyOf(List.of(ingredient)));
                    users.get(7).setIngredients(Set.copyOf(List.of(ingredient)));
                }
                ingredientsRepository.save(ingredient);
                userRepository.saveAll(users);
            }
            LOGGER.debug("generating {} ingredients entries", NUMBER_OF_INGREDIENTS_TO_GENERATE);
        }
    }
}
