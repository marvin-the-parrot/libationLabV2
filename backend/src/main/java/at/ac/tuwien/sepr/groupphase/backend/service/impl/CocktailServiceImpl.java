package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CocktailDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CocktailListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CocktailSerachDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CocktailTagSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.IngredientGroupDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.IngredientListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PreferenceListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.CocktailIngredientMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.IngredientMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.PreferenceMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Cocktail;
import at.ac.tuwien.sepr.groupphase.backend.entity.CocktailIngredients;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredient;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.CocktailIngredientsRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.CocktailRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.IngredientsRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PreferenceRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.CocktailService;
import at.ac.tuwien.sepr.groupphase.backend.service.IngredientService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Cocktail service implementation.
 */
@Service
public class CocktailServiceImpl implements CocktailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final CocktailIngredientsRepository cocktailIngredientsRepository;
    private final CocktailRepository cocktailRepository;
    private final IngredientsRepository ingredientsRepository;
    private final CocktailIngredientMapper cocktailIngredientMapper;
    private final IngredientService ingredientService;
    private final IngredientMapper ingredientMapper;
    private final PreferenceRepository preferenceRepository;
    private final PreferenceMapper preferenceMapper;

    @Autowired
    public CocktailServiceImpl(CocktailIngredientsRepository cocktailIngredientsRepository, CocktailRepository cocktailRepository,
                               IngredientsRepository ingredientsRepository, CocktailIngredientMapper cocktailIngredientMapper,
                               IngredientService ingredientService, IngredientMapper ingredientMapper, PreferenceRepository preferenceRepository,
                               PreferenceMapper preferenceMapper) {

        this.cocktailIngredientsRepository = cocktailIngredientsRepository;
        this.cocktailRepository = cocktailRepository;
        this.ingredientsRepository = ingredientsRepository;
        this.cocktailIngredientMapper = cocktailIngredientMapper;
        this.ingredientService = ingredientService;
        this.ingredientMapper = ingredientMapper;
        this.preferenceRepository = preferenceRepository;
        this.preferenceMapper = preferenceMapper;
    }

    @Override
    @Transactional
    public List<CocktailListDto> searchCocktails(CocktailSerachDto searchParameters) {
        if (searchParameters.getCocktailName() == null && searchParameters.getIngredientsName() == null && searchParameters.getPreferenceName() == null) {
            List<CocktailListDto> results = cocktailIngredientMapper.cocktailIngredientToCocktailListDto(cocktailRepository.findAll());
            // Sorting the result list by name
            results.sort(Comparator.comparing(CocktailListDto::getName));
            return results;
        }
        //Transform CocktailSerachDto to CocktailTagSearchDto (for easier handling)
        CocktailTagSearchDto searchTagParameters = new CocktailTagSearchDto();
        searchTagParameters.setCocktailName(searchParameters.getCocktailName());

        if (searchParameters.getIngredientsName() != null && !searchParameters.getIngredientsName().isEmpty()) {
            List<String> ingredientsList = Arrays.asList(searchParameters.getIngredientsName().split(","));
            searchTagParameters.setIngredientsName(ingredientsList);
        }
        if (searchParameters.getPreferenceName() != null && !searchParameters.getPreferenceName().isEmpty()) {
            List<String> preferencesList = Arrays.asList(searchParameters.getPreferenceName().split(","));
            searchTagParameters.setPreferenceName(preferencesList);
        }


        List<Cocktail> resultCocktails = new ArrayList<>();
        List<Cocktail> cocktails;
        //Search for cocktails by name
        if (searchParameters.getCocktailName() != null) {
            cocktails = cocktailRepository.findByNameContainingIgnoreCase(searchParameters.getCocktailName());
        } else {
            cocktails = cocktailRepository.findAll();
        }
        //Search for cocktails by ingredients
        if (searchParameters.getIngredientsName() != null && !searchParameters.getIngredientsName().isEmpty()) {

            //TODO: i dont know how to do it differently
            List<Cocktail> cocktailsByIngredients1 = null;
            for (String ingredient : searchTagParameters.getIngredientsName()) {
                List<CocktailIngredients> cocktailIngredients =
                    cocktailIngredientsRepository.findByIngredientNameEqualsIgnoreCase(ingredient);
                List<Cocktail> cocktailsByIngredients2 = cocktailRepository.findDistinctByCocktailIngredientsIn(cocktailIngredients);

                if (cocktailsByIngredients1 == null) {
                    cocktailsByIngredients1 = new ArrayList<>(cocktailsByIngredients2);
                } else {
                    cocktailsByIngredients1.retainAll(cocktailsByIngredients2);
                }

                if (cocktailsByIngredients1.isEmpty()) {
                    break;
                }
            }

            //Cocktails added to List which match name and ingredients
            if (!cocktails.isEmpty()) {
                for (Cocktail cocktail : cocktails) {
                  assert cocktailsByIngredients1 != null;
                    for (Cocktail cocktailIngredient : cocktailsByIngredients1) {
                        if (cocktail.getName().equals(cocktailIngredient.getName())) {
                            resultCocktails.add(cocktail);
                        }
                    }
                }
            }
        } else {
            //Add all cocktails from name search to result list if no ingredients are given
            resultCocktails.addAll(cocktails);
        }
        //Search for cocktails by every preference
        if (searchParameters.getPreferenceName() != null && !searchParameters.getPreferenceName().isEmpty()) {
            for (String preference : searchTagParameters.getPreferenceName()) {
                List<Cocktail> cocktailsByPreferences = cocktailRepository.findByPreferencesIn(preferenceRepository.findByNameEqualsIgnoreCase(preference));
                //Filter out cocktails which do not have specific preference
                resultCocktails = resultCocktails.stream().filter(cocktailsByPreferences::contains).collect(Collectors.toList());
            }
        }

        return cocktailIngredientMapper.cocktailIngredientToCocktailListDto(resultCocktails);
    }




    @Override
    @Transactional
    public List<CocktailDetailDto> getMixableCocktails(Long groupId) {
        LOGGER.debug("Get all mixable cocktails");

        // get all ingredients
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
            // List<Ingredient> cocktailIngredients = ingredientsRepository.findByCocktailIngredientsIn(cocktailIngredientsRepository.findAllByCocktail(cocktail));
            List<Ingredient> cocktailIngredients = cocktail.getCocktailIngredients().stream().map(CocktailIngredients::getIngredient).toList();

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

        List<CocktailDetailDto> cocktailOverviewDto = new ArrayList<>();
        for (Cocktail cocktail : cocktailsWithAllIngredients) {
            cocktailOverviewDto.add(cocktailIngredientMapper.cocktailToCocktailDetailDto(cocktail));
        }
        return cocktailOverviewDto;
    }

    @Transactional
    @Override
    public List<IngredientListDto> searchAutoIngredients(String searchParams) {
        return ingredientMapper.ingredientToIngredientListDto(ingredientsRepository.findFirst10ByNameIgnoreCaseContainingOrderByName(searchParams));
    }

    @Transactional
    @Override
    public List<PreferenceListDto> searchAutoPreferences(String searchParams) {
        return preferenceMapper.preferenceToPreferenceListDto(preferenceRepository.findFirst10ByNameIgnoreCaseContainingOrderByName(searchParams));
    }

    @Override
    @Transactional
    public CocktailDetailDto getCocktailById(Long id) throws NotFoundException {
        LOGGER.debug("Get cocktail by id {}", id);
        Cocktail cocktail = cocktailRepository.findById(id).orElse(null);
        if (cocktail == null) {
            throw new NotFoundException("Cocktail with id " + id + " not found");
        }
        return cocktailIngredientMapper.cocktailToCocktailDetailDto(cocktail);
    }

}
