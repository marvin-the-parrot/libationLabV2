package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CocktailOverviewDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Cocktail;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredient;
import at.ac.tuwien.sepr.groupphase.backend.repository.CocktailRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.IngredientsRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
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
    private IngredientsRepository ingredientsRepository;

    @Autowired
    private CocktailIngredientMapper cocktailIngredientMapper;

    @Override
    public List<CocktailListDto> searchCocktailByCocktailNameAndIngredientName(String cocktailsName,
                                                                               String ingredientsName) {
        LOGGER.debug("Search for cocktail by cocktail name and ingredient name {}", cocktailsName, ingredientsName);
        if (cocktailsName == null) {
            return cocktailIngredientMapper.cocktailIngredientToCocktailListDto(cocktailIngredientsRepository.findByIngredientName(ingredientsName));
        } else if (ingredientsName == null) {
            return cocktailIngredientMapper.cocktailIngredientToCocktailListDto(cocktailIngredientsRepository.findByCocktailName(cocktailsName));
        }
        return cocktailIngredientMapper.cocktailIngredientToCocktailListDto(
            cocktailIngredientsRepository.findByIngredientNameAndCocktailName(ingredientsName, cocktailsName));
    }

    @Override
    @Transactional
    public List<CocktailOverviewDto> getMixableCocktails(Long groupId) {
        LOGGER.debug("Get all mixable cocktails");

        // get all ingredients

        // get all ingredients from user
        ApplicationUser host = userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        List<Ingredient> availableIngredients = ingredientsRepository.findAllByApplicationUser(host);
        // Todo: implement get all ingredients from group members

        // get all cocktails
        List<Cocktail> cocktails = cocktailRepository.findAllByOrderByNameAsc();

        // check if cocktail can be mixed with ingredients
        // if yes add cocktail to list
        List<Cocktail> cocktailsWithAllIngredients = new ArrayList<>();
        for (Cocktail cocktail : cocktails) {
            List<Ingredient> cocktailIngredients = ingredientsRepository.findByCocktailIngredientsIn(cocktailIngredientsRepository.findAllByCocktail(cocktail));

            boolean hasAllIngredients = true;
            for (Ingredient ingredient : cocktailIngredients) {
                if (!availableIngredients.contains(ingredient)) {
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
