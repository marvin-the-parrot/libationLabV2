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
import java.util.Map;
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
        //if no search parameters are given, return all cocktails
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

            cocktails = cocktailRepository.findCocktailsWithIngredients(ingredientsString, ingredientsString.size());

            if (cocktails.isEmpty()) {
                return new ArrayList<>();
            }

        } else if (searchParameters.getIngredientsName() != null && !searchParameters.getIngredientsName().isEmpty()) {

            cocktails.retainAll(cocktailRepository.findCocktailsWithIngredients(ingredientsString, ingredientsString.size()));

            if (cocktails.isEmpty()) {
                return new ArrayList<>();
            }
        }

        // Filter Cocktails by preferences
        List<String> preferencesString = searchTagParameters.getPreferenceName();
        if (cocktails.isEmpty() && searchParameters.getPreferenceName() != null && !searchParameters.getPreferenceName().isEmpty()) {

            cocktails = cocktailRepository.findCocktailsWithPreferences(preferencesString, preferencesString.size());

            if (cocktails.isEmpty()) {
                return new ArrayList<>();
            }
        } else if (searchParameters.getPreferenceName() != null && !searchParameters.getPreferenceName().isEmpty()) {
            cocktails.retainAll(cocktailRepository.findCocktailsWithPreferences(preferencesString, preferencesString.size()));

            if (cocktails.isEmpty()) {
                return new ArrayList<>();
            }
        }

        //Map to CocktailListDto
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
        List<String> names = availableIngredients.stream().map(IngredientGroupDto::getName).toList();
        List<Ingredient> ingredients = ingredientsRepository.findByNameIn(names);

        // fetch how many ingredients each cocktail has
        List<Object[]> cocktailIngredientsCount = cocktailIngredientsRepository.countIngredientsByCocktail();
        Map<Long, Long> cocktailIngredientsCountMap = cocktailIngredientsCount.stream().collect(Collectors.toMap(
            cocktail -> (Long) cocktail[0],
            cocktail -> (Long) cocktail[1]
        ));

        // fetch how many ingredients each cocktail has that are available
        List<Object[]> cocktailAvailableIngredientsCount = cocktailIngredientsRepository.countIngredientsByCocktailsIn(ingredients);
        Map<Long, Long> cocktailAvailableIngredientsCountMap = cocktailAvailableIngredientsCount.stream().collect(Collectors.toMap(
            cocktail -> (Long) cocktail[0],
            cocktail -> (Long) cocktail[1]
        ));

        // compare the two maps and get the cocktails that have all ingredients available
        List<Long> mixableCocktailIds = new ArrayList<>();
        for (Map.Entry<Long, Long> entry : cocktailIngredientsCountMap.entrySet()) {
            if (entry.getValue().equals(cocktailAvailableIngredientsCountMap.get(entry.getKey()))) {
                mixableCocktailIds.add(entry.getKey());
            }
        }

        // fetch all available cocktails from database
        List<Cocktail> mixableCocktails = cocktailRepository.findAllById(mixableCocktailIds);

        List<CocktailDetailDto> cocktailOverviewDto = new ArrayList<>();
        for (Cocktail cocktail : mixableCocktails) {
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
