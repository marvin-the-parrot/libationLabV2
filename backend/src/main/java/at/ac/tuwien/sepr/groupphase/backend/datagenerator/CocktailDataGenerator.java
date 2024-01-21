package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Cocktail;
import at.ac.tuwien.sepr.groupphase.backend.entity.CocktailIngredients;
import at.ac.tuwien.sepr.groupphase.backend.entity.CocktailIngredientsKey;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredient;
import at.ac.tuwien.sepr.groupphase.backend.entity.Preference;
import at.ac.tuwien.sepr.groupphase.backend.repository.CocktailIngredientsRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.IngredientsRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PreferenceRepository;
import com.opencsv.CSVReader;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import at.ac.tuwien.sepr.groupphase.backend.repository.CocktailRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserGroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.Map;
import java.util.Set;

/**
 * Group Data Generator.
 */
@Profile("generateData")
@Component
public class CocktailDataGenerator {

    private static final Logger LOGGER =
        LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final UserGroupRepository userGroupRepository;
    private final CocktailRepository cocktailRepository;
    private final IngredientsRepository ingredientsRepository;
    private final PreferenceRepository preferenceRepository;
    private final CocktailIngredientsRepository cocktailIngredeintsRepository;

    public CocktailDataGenerator(GroupRepository groupRepository, UserRepository userRepository, UserGroupRepository userGroupRepository,
                                 CocktailRepository cocktailRepository,
                                 IngredientsRepository ingredientsRepository,
                                 PreferenceRepository preferenceRepository,
                                 CocktailIngredientsRepository cocktailIngredeintsRepository) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.userGroupRepository = userGroupRepository;
        this.cocktailRepository = cocktailRepository;
        this.ingredientsRepository = ingredientsRepository;
        this.preferenceRepository = preferenceRepository;
        this.cocktailIngredeintsRepository = cocktailIngredeintsRepository;
    }

    @PostConstruct
    private void generateGroup() throws IOException {
        if (cocktailRepository.findAll().size() > 0) {
            LOGGER.debug("cocktail data already generated");
        } else {

            ingestIngredients();
            ingestPreferences();
            giveUsersIngredients();
            ingestCocktailsWithIngredientsAndPreferences();
            giveUsersPreferences();

        }
    }

    private void giveUsersPreferences() {
        ApplicationUser user = userRepository.findById(1L).orElse(null);
        List<String> preferenceNames1 = List.of("Sweet", "Tequila", "Whiskey", "Rum", "Alcoholic", "Martini glass");
        Set<Preference> updatedPreferences = new HashSet<>();


        for (String preferenceName : preferenceNames1) {
            List<Preference> preference = preferenceRepository.findByNameEqualsIgnoreCase(preferenceName);
            updatedPreferences.add(preference.get(0));
        }
        user.setPreferences(updatedPreferences);
        userRepository.save(user);

        user = userRepository.findById(3L).orElse(null);
        List<String> preferenceNames2 = List.of("Sweet", "Whiskey", "Lillet");

        updatedPreferences.clear();

        for (String preferenceName : preferenceNames2) {
            List<Preference> preference = preferenceRepository.findByNameEqualsIgnoreCase(preferenceName);
            updatedPreferences.add(preference.get(0));
        }
        user.setPreferences(updatedPreferences);
        userRepository.save(user);

    }

    private void giveUsersIngredients() {
        List<ApplicationUser> users = userRepository.findAll();
        Collections.reverse(users);

        List<Ingredient> ingredients = ingredientsRepository.findAll();
        for (ApplicationUser user : users) {
            Set<Ingredient> ingredientSet = Set.copyOf(ingredients);
            user.setIngredients(ingredientSet);
            userRepository.save(user);

            //remove 10 ingredients from the list
            ingredients.subList(0, 15).clear();
        }

    }

    private void ingestCocktailsWithIngredientsAndPreferences() {
        // generate a new file
        Path path = Paths.get("src", "main", "java", "at", "ac", "tuwien", "sepr", "groupphase", "backend", "datagenerator", "dataset", "cocktails.csv");

        try (CSVReader reader = new CSVReaderBuilder(new FileReader(path.toString())).withSkipLines(1).build()) {
            List<String[]> lines = reader.readAll();
            List<Preference> allPreferences = preferenceRepository.findAll();
            Map<String, Preference> preferenceMap = new HashMap<>();
            for (Preference preference : allPreferences) {
                preferenceMap.put(preference.getName(), preference);
            }

            for (String[] line : lines) {
                String strDrink = line[1];
                String strInstructions = line[2];
                String strDrinkThumb = line[4];

                Cocktail cocktail = new Cocktail();
                cocktail.setName(strDrink);
                cocktail.setInstructions(strInstructions);
                cocktail.setImagePath(strDrinkThumb);


                String strPreferences = line[5];
                // Remove curly braces and split into key-value pairs
                // remove quotes
                strPreferences = strPreferences.replaceAll("\'", "");
                String[] preferenceValues = strPreferences.substring(1, strPreferences.length() - 1).split(", ");

                // create empty list
                Set<Preference> preferenceSet = new HashSet<>();
                for (String preferenceName : preferenceValues) {
                    Preference preference = preferenceMap.get(preferenceName);
                    preferenceSet.add(preference);
                }

                cocktail.setPreferences(preferenceSet);
                cocktailRepository.save(cocktail);

                String strIngredients = line[3];
                // Remove curly braces and split into key-value pairs
                String[] pairs = strIngredients.substring(1, strIngredients.length() - 1).split(", ");

                // Create a Map to store key-value pairs
                Map<String, String> ingredients = new HashMap<>();

                for (String pair : pairs) {
                    String[] keyValue = pair.split(": ");
                    String key = keyValue[0].replaceAll("'", ""); // Remove single quotes
                    String value = keyValue[1].replaceAll("'", ""); // Remove single quotes

                    ingredients.put(key, value);
                }

                for (String ingredientName : ingredients.keySet()) {
                    CocktailIngredients cocktailIngredients = new CocktailIngredients();
                    Ingredient ingredient = ingredientsRepository.findByName(ingredientName);

                    cocktailIngredients.setCocktailIngredientsKey(new CocktailIngredientsKey(cocktail.getId(), ingredient.getId()));
                    cocktailIngredients.setCocktail(cocktail);
                    cocktailIngredients.setIngredient(ingredient);
                    cocktailIngredients.setQuantity(ingredients.get(ingredientName));
                    cocktailIngredeintsRepository.save(cocktailIngredients);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CsvException e) {
            e.printStackTrace();
        }
    }

    private void ingestIngredients() {
        // generate Ingredients
        Path path = Paths.get("src", "main", "java", "at", "ac", "tuwien", "sepr", "groupphase", "backend", "datagenerator", "dataset", "ingredients.csv");
        LOGGER.debug("Reading ingredients from file: {}", path);

        try (CSVReader reader = new CSVReaderBuilder(new FileReader(path.toString())).withSkipLines(1).build()) {
            List<String[]> lines = reader.readAll();
            LOGGER.debug("loading {} ingredient entries", lines.size());

            for (String[] line : lines) {
                Ingredient ingredient = Ingredient.IngredientsBuilder.ingredients()
                    .withName(line[0])
                    .build();
                ingredientsRepository.save(ingredient);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CsvException e) {
            e.printStackTrace();
        }
    }

    private void ingestPreferences() {
        // generate Preferences

        Path path = Paths.get("src", "main", "java", "at", "ac", "tuwien", "sepr", "groupphase", "backend", "datagenerator", "dataset", "tags.csv");
        LOGGER.debug("Reading tags from file: {}", path);

        try (CSVReader reader = new CSVReaderBuilder(new FileReader(path.toString())).withSkipLines(1).build()) {
            List<String[]> lines = reader.readAll();
            LOGGER.debug("loading {} tag entries", lines.size());

            for (String[] line : lines) {
                Preference preference = Preference.PreferencesBuilder.preferences()
                    .withName(line[0])
                    .build();
                preferenceRepository.save(preference);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CsvException e) {
            e.printStackTrace();
        }

    }
}
