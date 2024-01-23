package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CocktailDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CocktailListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CocktailSerachDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CocktailTagSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.IngredientGroupDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.IngredientListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PreferenceListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.CocktailIngredientMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.CocktailTagSearchDtoMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.IngredientMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.PreferenceMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Cocktail;
import at.ac.tuwien.sepr.groupphase.backend.entity.CocktailIngredients;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredient;
import at.ac.tuwien.sepr.groupphase.backend.entity.Preference;
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
    private final CocktailTagSearchDtoMapper cocktailTagSearchDtoMapper;

    @Autowired
    public CocktailServiceImpl(CocktailIngredientsRepository cocktailIngredientsRepository, CocktailRepository cocktailRepository,
                               IngredientsRepository ingredientsRepository, CocktailIngredientMapper cocktailIngredientMapper,
                               IngredientService ingredientService, IngredientMapper ingredientMapper, PreferenceRepository preferenceRepository,
                               PreferenceMapper preferenceMapper, CocktailTagSearchDtoMapper cocktailTagSearchDtoMapper) {

        this.cocktailIngredientsRepository = cocktailIngredientsRepository;
        this.cocktailRepository = cocktailRepository;
        this.ingredientsRepository = ingredientsRepository;
        this.cocktailIngredientMapper = cocktailIngredientMapper;
        this.ingredientService = ingredientService;
        this.ingredientMapper = ingredientMapper;
        this.preferenceRepository = preferenceRepository;
        this.preferenceMapper = preferenceMapper;
        this.cocktailTagSearchDtoMapper = cocktailTagSearchDtoMapper;
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

        //Map the search parameters to the CocktailTagSearchDto, for easies filtering
        CocktailTagSearchDto searchTagParameters = cocktailTagSearchDtoMapper.cocktailSearchDtoToCocktailTagSearchDto(searchParameters);

        List<Cocktail> cocktails = new ArrayList<>();

        //Search for cocktails by name
        if (searchParameters.getCocktailName() != null) {
            cocktails = cocktailRepository.findByNameContainingIgnoreCase(searchParameters.getCocktailName());
            if (cocktails.isEmpty()) {
                return new ArrayList<>();
            }
        }

        // Filter Cocktails by ingredients
        List<String> ingredientsString = searchTagParameters.getIngredientsName();
        if (cocktails.isEmpty() && searchParameters.getIngredientsName() != null && !searchParameters.getIngredientsName().isEmpty()) {
            List<CocktailIngredients> cocktailIngredients = cocktailIngredientsRepository.findByIngredientNameIgnoreCase(searchTagParameters.getIngredientsName().get(0));
            cocktails = cocktailRepository.findDistinctByCocktailIngredientsIn(cocktailIngredients);

            cocktails = filterCocktailsByIngredients(cocktails, ingredientsString);

            if (cocktails.isEmpty()) {
                return new ArrayList<>();
            }

        } else if (searchParameters.getIngredientsName() != null && !searchParameters.getIngredientsName().isEmpty()) {

            cocktails = filterCocktailsByIngredients(cocktails, ingredientsString);

            if (cocktails.isEmpty()) {
                return new ArrayList<>();
            }
        }

        // Filter Cocktails by preferences
        List<String> preferencesString = searchTagParameters.getPreferenceName();
        if (cocktails.isEmpty() && searchParameters.getPreferenceName() != null && !searchParameters.getPreferenceName().isEmpty()) {
            Preference preferenceOne = preferenceRepository.findByName(searchTagParameters.getPreferenceName().get(0));
            cocktails = cocktailRepository.findByPreferences(preferenceOne);

            if (searchTagParameters.getPreferenceName().size() > 1) {
                cocktails = filterCocktailsByPreferences(cocktails, preferencesString);
            }
            if (cocktails.isEmpty()) {
                return new ArrayList<>();
            }
        } else if (searchParameters.getPreferenceName() != null && !searchParameters.getPreferenceName().isEmpty()) {
            cocktails = filterCocktailsByPreferences(cocktails, preferencesString);

            if (cocktails.isEmpty()) {
                return new ArrayList<>();
            }
        }

        List<CocktailListDto> results = cocktailIngredientMapper.cocktailIngredientToCocktailListDto(cocktails);
        // Sorting the result list by name
        results.sort(Comparator.comparing(CocktailListDto::getName));
        return results;
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

    private List<Cocktail> filterCocktailsByIngredients(List<Cocktail> cocktails, List<String> ingredients) {
        return cocktails.stream()
            .filter(cocktail ->
                ingredients.stream().allMatch(ingredient ->
                    cocktail.getCocktailIngredients().stream()
                        .anyMatch(ci ->
                            ci.getIngredient().getName().equalsIgnoreCase(ingredient)
                        )
                )
            )
            .toList();
    }

    private List<Cocktail> filterCocktailsByPreferences(List<Cocktail> cocktails, List<String> preferences) {
        return cocktails.stream()
            .filter(cocktail ->
                preferences.stream().allMatch(preference ->
                    cocktail.getPreferences().stream()
                        .anyMatch(cocktailPreference ->
                            cocktailPreference.getName().equalsIgnoreCase(preference)
                        )
                )
            )
            .toList();
    }

}
