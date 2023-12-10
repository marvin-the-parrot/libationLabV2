package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CocktailOverviewDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.IngredientGroupDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Cocktail;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredient;
import at.ac.tuwien.sepr.groupphase.backend.repository.CocktailRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.IngredientsRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.IngredientService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CocktailListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.CocktailIngredientMapper;
import at.ac.tuwien.sepr.groupphase.backend.repository.CocktailIngredientsRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.CocktailIngredientService;

/**
 * Cocktail service implementation.
 */
@Service
public class CocktailServiceImpl implements CocktailIngredientService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private CocktailIngredientsRepository cocktailIngredientsRepository;

    @Autowired
    private CocktailRepository cocktailRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private IngredientsRepository ingredientsRepository;

    @Autowired
    private CocktailIngredientMapper cocktailIngredientMapper;

    @Autowired
    private IngredientService ingredientService;

    @Override
    public List<CocktailListDto> searchCocktailByCocktailNameAndIngredientName(String cocktailsName,
                                                                               String ingredientsName) {
        LOGGER.debug("Search for cocktail by cocktail name and ingredient name {}", cocktailsName, ingredientsName);
        if (cocktailsName == null) {
            return cocktailIngredientMapper.cocktailIngredientToCocktailListDto(cocktailIngredientsRepository.findByIngredientNameContainingIgnoreCase(ingredientsName));
        } else if (ingredientsName == null) {
            return cocktailIngredientMapper.cocktailIngredientToCocktailListDto(cocktailIngredientsRepository.findByCocktailNameContainingIgnoreCase(cocktailsName));
        }
        return cocktailIngredientMapper.cocktailIngredientToCocktailListDto(cocktailIngredientsRepository.findByIngredientNameContainingIgnoreCaseAndCocktailNameContainingIgnoreCase(ingredientsName, cocktailsName));
    }

    @Override
    @Transactional
    public List<CocktailOverviewDto> getMixableCocktails(Long groupId) {
        LOGGER.debug("Get all mixable cocktails");

        // get all ingredients

        // get all ingredients from user
        ApplicationUser host = userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        // Todo: implement get all ingredients from group members

        List<IngredientGroupDto> availableIngredients = ingredientService.getAllGroupIngredients(groupId);
        // TODO: use one function for both cases
        List<String> availableIngredientsList = new ArrayList<>();
        for (IngredientGroupDto ingredientGroupDto : availableIngredients) {
            availableIngredientsList.add(ingredientGroupDto.getName());
        }


        // get all cocktails
        List<Cocktail> cocktails = cocktailRepository.findAllByOrderByNameAsc();

        // check if cocktail can be mixed with ingredients
        // if yes add cocktail to list
        List<Cocktail> cocktailsWithAllIngredients = new ArrayList<>();
        for (Cocktail cocktail : cocktails) {
            List<Ingredient> cocktailIngredients = ingredientsRepository.findByCocktailIngredientsIn(cocktailIngredientsRepository.findAllByCocktail(cocktail));

            boolean hasAllIngredients = true;
            for (Ingredient ingredient : cocktailIngredients) {
                if (!availableIngredientsList.contains(ingredient.getName())) {
                    hasAllIngredients = false;
                    break;
                }
            }

            if (hasAllIngredients) {
                cocktailsWithAllIngredients.add(cocktail);
            }
        }

        List<CocktailOverviewDto> cocktailOverviewDto = new ArrayList<>();
        for (Cocktail cocktail : cocktailsWithAllIngredients) {
            cocktailOverviewDto.add(cocktailIngredientMapper.cocktailToCocktailOverviewDto(cocktail));
        }
        return cocktailOverviewDto;
    }

}
