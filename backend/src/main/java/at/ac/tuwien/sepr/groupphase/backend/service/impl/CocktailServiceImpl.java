package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CocktailDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CocktailListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CocktailOverviewDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CocktailSerachDto;
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

        List<Cocktail> resultCocktails = new ArrayList<>();
        List<Cocktail> cocktails;
        if (searchParameters.getCocktailName() != null) {
            cocktails = cocktailRepository.findByNameContainingIgnoreCase(searchParameters.getCocktailName());
        } else {
            cocktails = cocktailRepository.findAll();
        }
        if (searchParameters.getIngredientsName() != null) {
            List<CocktailIngredients> cocktailIngredients =
                cocktailIngredientsRepository.findByIngredientNameContainingIgnoreCase(searchParameters.getIngredientsName());
            List<Cocktail> cocktailsByIngredients = cocktailRepository.findDistinctByCocktailIngredientsIn(cocktailIngredients);

            if (!cocktails.isEmpty()) {
                for (Cocktail cocktail : cocktails) {
                    for (Cocktail cocktailIngredient : cocktailsByIngredients) {
                        if (cocktail.getName().equals(cocktailIngredient.getName())) {
                            resultCocktails.add(cocktail);
                        }
                    }
                }
            }
        } else {
            resultCocktails.addAll(cocktails);
        }
        if (searchParameters.getPreferenceName() != null) {
            List<Cocktail> cocktailPreferences =
                cocktailRepository.findByPreferencesIn(preferenceRepository.findByNameContainingIgnoreCase(searchParameters.getPreferenceName()));

            resultCocktails = resultCocktails.stream().filter(cocktailPreferences::contains).collect(Collectors.toList());
        }

        return cocktailIngredientMapper.cocktailIngredientToCocktailListDto(resultCocktails);
    }


    /*@Override
    public List<CocktailListDto> searchCocktailByCocktailNameAndIngredientName(String cocktailsName, String ingredientsName, String preferenceName) {
        LOGGER.debug("Search for cocktail by cocktail name and ingredient name {}", cocktailsName, ingredientsName, preferenceName);
        if (cocktailsName == null && preferenceName == null) {
            return getMappedCocktailsByIngredient(ingredientsName);
        } else if (ingredientsName == null && preferenceName == null) {
            return cocktailIngredientMapper.cocktailIngredientToCocktailListDto(cocktailIngredientsRepository.findByCocktailNameContainingIgnoreCase(cocktailsName));
        } else if (preferenceName == null) {
            return getMappedCocktailsByCocktailAndIngredient(cocktailsName, ingredientsName);
        } else if (cocktailsName == null && ingredientsName == null) {
            List<CocktailListDto> cocktailPreferenceToCocktailListDto = getMappedCocktailsByPerformance(preferenceName);
            return addedIngredientsToCocktailList(cocktailPreferenceToCocktailListDto);
        } else if (cocktailsName == null) {
            List<CocktailListDto> cocktailsPerformance = getMappedCocktailsByPerformance(preferenceName);
            List<CocktailListDto> cocktailsIngredient = getMappedCocktailsByIngredient(ingredientsName);
            return cocktailsIngredient.stream().filter(ingredientDto -> cocktailsPerformance.stream().anyMatch(performanceDto -> performanceDto.getName().equals(ingredientDto.getName()))).toList();
        } else if (ingredientsName == null) {
            List<CocktailListDto> cocktailsPerformance = getMappedCocktailsByCocktailAndPerformance(cocktailsName, preferenceName);
            List<CocktailListDto> cocktailsIngredient = cocktailIngredientMapper.cocktailIngredientToCocktailListDto(cocktailIngredientsRepository.findByCocktailNameContainingIgnoreCase(cocktailsName));
            return cocktailsIngredient.stream().filter(ingredientDto -> cocktailsPerformance.stream().anyMatch(performanceDto -> performanceDto.getName().equals(ingredientDto.getName()))).toList();
        } else {
            List<CocktailListDto> cocktailsPerformance = getMappedCocktailsByCocktailAndPerformance(cocktailsName, preferenceName);
            List<CocktailListDto> cocktailsIngredient = getMappedCocktailsByCocktailAndIngredient(cocktailsName, ingredientsName);
            return cocktailsIngredient.stream().filter(ingredientDto -> cocktailsPerformance.stream().anyMatch(performanceDto -> performanceDto.getName().equals(ingredientDto.getName()))).toList();
        }
    }

    private List<CocktailListDto> getMappedCocktailsByPerformance(String preferenceName) {
        List<CocktailPreference> preferenceByName = cocktailPreferenceRepository.findByPreferenceNameContainingIgnoreCase(preferenceName);
        List<CocktailPreference> cocktailByPreference = new ArrayList<>();
        preferenceByName.forEach(preference -> cocktailByPreference.addAll(cocktailPreferenceRepository.findByCocktailNameContainingIgnoreCase(preference.getCocktail().getName())));
        List<CocktailListDto> cocktailPreferenceToCocktailListDto = cocktailPreferenceMapper.cocktailPreferenceToCocktailListDto(cocktailByPreference);
        return cocktailPreferenceToCocktailListDto;
    }

    private List<CocktailListDto> getMappedCocktailsByIngredient(String ingredientsName) {
        List<CocktailIngredients> ingredientsByName = cocktailIngredientsRepository.findByIngredientNameContainingIgnoreCase(ingredientsName);
        List<CocktailIngredients> cocktailsByIngredientsName = new ArrayList<>();
        ingredientsByName.forEach(ingredient -> cocktailsByIngredientsName.addAll(cocktailIngredientsRepository.findByCocktailNameContainingIgnoreCase(ingredient.getCocktail().getName())));
        return cocktailIngredientMapper.cocktailIngredientToCocktailListDto(cocktailsByIngredientsName);
    }

    private List<CocktailListDto> addedIngredientsToCocktailList(List<CocktailListDto> cocktailsList) {
        List<CocktailListDto> cocktailListDtos = new ArrayList<>();
        for (CocktailListDto eachCocktail : cocktailsList) {
            List<CocktailIngredients> cocktailsIngredinets = this.cocktailIngredientsRepository.findByCocktailNameContainingIgnoreCase(eachCocktail.getName());
            for (CocktailIngredients eachCocktailIngredients : cocktailsIngredinets) {
                List<String> ingredientsName = eachCocktail.getIngredientsName();
                ingredientsName.add(eachCocktailIngredients.getIngredient().getName());
                eachCocktail.setIngredientsName(ingredientsName);
            }
            cocktailListDtos.add(eachCocktail);
        }
        return cocktailListDtos;
    }

    private List<CocktailListDto> getMappedCocktailsByCocktailAndIngredient(String cocktailsName, String ingredientsName) {
        List<CocktailIngredients> cocktailsAndIngredients = cocktailIngredientsRepository.findByIngredientNameContainingIgnoreCaseAndCocktailNameContainingIgnoreCase(ingredientsName, cocktailsName);
        List<CocktailIngredients> cocktailsByIngredientsName = new ArrayList<>();
        cocktailsAndIngredients.forEach(ingredient -> cocktailsByIngredientsName.addAll(cocktailIngredientsRepository.findByCocktailNameContainingIgnoreCase(ingredient.getCocktail().getName())));
        return cocktailIngredientMapper.cocktailIngredientToCocktailListDto(cocktailsByIngredientsName);
    }

    private List<CocktailListDto> getMappedCocktailsByCocktailAndPerformance(String cocktailsName, String preferenceName) {
        List<CocktailPreference> cocktailByNameAndPreference = cocktailPreferenceRepository.findByPreferenceNameContainingIgnoreCaseAndCocktailNameContainingIgnoreCase(preferenceName, cocktailsName);
        List<CocktailPreference> cocktailByPreference = new ArrayList<>();
        cocktailByNameAndPreference.forEach(preference -> cocktailByPreference.addAll(cocktailPreferenceRepository.findByCocktailNameContainingIgnoreCase(preference.getCocktail().getName())));
        return cocktailPreferenceMapper.cocktailPreferenceToCocktailListDto(cocktailByPreference);
    }*/

    @Override
    @Transactional
    public List<CocktailOverviewDto> getMixableCocktails(Long groupId) {
        LOGGER.debug("Get all mixable cocktails");

        // get all ingredients

        // get all ingredients from user
        //ApplicationUser host = userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
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

    @Transactional
    @Override
    public List<IngredientListDto> searchAutoIngredients(String searchParams) {
        return ingredientMapper.ingredientToIngredientListDto(ingredientsRepository.findFirst5ByNameIgnoreCaseContainingOrderByName(searchParams));
    }

    @Transactional
    @Override
    public List<PreferenceListDto> searchAutoPreferences(String searchParams) {
        return preferenceMapper.preferenceToPreferenceListDto(preferenceRepository.findFirst5ByNameIgnoreCaseContainingOrderByName(searchParams));
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
