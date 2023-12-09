package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.Cocktail;
import at.ac.tuwien.sepr.groupphase.backend.entity.CocktailIngredients;
import at.ac.tuwien.sepr.groupphase.backend.entity.CocktailIngredientsKey;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredient;
import at.ac.tuwien.sepr.groupphase.backend.repository.CocktailIngredientsRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.IngredientsRepository;
import com.opencsv.CSVReader;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
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
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.Map;

/**
 * Group Data Generator.
 */
@Profile("generateData")
@Component
public class CocktailDataGenerator {

    private static final Logger LOGGER =
        LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final UserGroupRepository userGroupRepository;
    private final CocktailRepository cocktailRepository;
    private final IngredientsRepository ingredientsRepository;
    private final CocktailIngredientsRepository cocktailIngredeintsRepository;

    public CocktailDataGenerator(GroupRepository groupRepository, UserRepository userRepository, UserGroupRepository userGroupRepository,
                                 CocktailRepository cocktailRepository,
                                 IngredientsRepository ingredientsRepository,
                                 CocktailIngredientsRepository cocktailIngredeintsRepository) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.userGroupRepository = userGroupRepository;
        this.cocktailRepository = cocktailRepository;
        this.ingredientsRepository = ingredientsRepository;
        this.cocktailIngredeintsRepository = cocktailIngredeintsRepository;
    }

    @PostConstruct
    private void generateGroup() throws IOException {
        if (cocktailRepository.findAll().size() > 0) {
            LOGGER.debug("group already generated");
        } else {

            // generate Ingredients
            Path path = Paths.get("src\\main\\java\\at\\ac\\tuwien\\sepr\\groupphase\\backend", "datagenerator", "dataset", "ingredients.csv");
            LOGGER.debug("Reading ingredients from file: {}", path);

            try (CSVReader reader = new CSVReaderBuilder(new FileReader(path.toString())).withSkipLines(1).build()) {
                List<String[]> lines = reader.readAll();
                LOGGER.debug("loading {} cocktail entries", lines.size());

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

            // generate a new file
            path = Paths.get("src\\main\\java\\at\\ac\\tuwien\\sepr\\groupphase\\backend", "datagenerator", "dataset", "cocktails.csv");

            try (CSVReader reader = new CSVReaderBuilder(new FileReader(path.toString())).withSkipLines(1).build()) {
                List<String[]> lines = reader.readAll();

                for (String[] line : lines) {
                    String strDrink = line[1];
                    //String strAlcoholic = line[2];
                    //String strGlass = line[3];
                    String strInstructions = line[4];
                    String strIngredients = line[5];
                    String strDrinkThumb = line[6];

                    Cocktail cocktail = new Cocktail();
                    cocktail.setName(strDrink);
                    cocktail.setInstructions(strInstructions);
                    cocktail.setImagePath(strDrinkThumb);
                    cocktailRepository.save(cocktail);


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
                        cocktailIngredeintsRepository.save(cocktailIngredients);
                    }


                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (CsvException e) {
                e.printStackTrace();
            }

        }
    }
}
