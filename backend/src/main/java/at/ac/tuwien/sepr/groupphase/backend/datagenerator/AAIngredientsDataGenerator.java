package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredient;
import at.ac.tuwien.sepr.groupphase.backend.repository.IngredientsRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

@Profile("generateData")
@Component
public class AAIngredientsDataGenerator {

    //TODO change name to IngredientsDataGenerator
    //the profile is started in alphabetical order and therefore there are no ingredients
    //available to set for the users in the many to many relationship
    private static final Logger LOGGER =
        LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int NUMBER_OF_INGREDIENTS_TO_GENERATE = 10;
    private final IngredientsRepository ingredientsRepository;

    public AAIngredientsDataGenerator(IngredientsRepository ingredientsRepository) {
        this.ingredientsRepository = ingredientsRepository;
    }

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
        }
    }
}
