package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CocktailOverviewDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Cocktail;
import at.ac.tuwien.sepr.groupphase.backend.repository.CocktailRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    private CocktailIngredientMapper cocktailIngredientMapper;

    @Override
    public List<CocktailListDto> searchCocktailByCocktailNameAndIngredientName(String cocktailsName,
                                                                               String ingredientsName) {
        LOGGER.debug("Search for cocktail by cocktail name and ingredient name {}", cocktailsName, ingredientsName);
        if (cocktailsName == null) {
            return cocktailIngredientMapper.cocktailIngredientToCocktailListDto(cocktailIngredientsRepository.findByIngredientName(ingredientsName));
        } else
            if (ingredientsName == null) {
                return cocktailIngredientMapper.cocktailIngredientToCocktailListDto(cocktailIngredientsRepository.findByCocktailName(cocktailsName));
            }
        return cocktailIngredientMapper.cocktailIngredientToCocktailListDto(cocktailIngredientsRepository.findByIngredientNameAndCocktailName(ingredientsName, cocktailsName));
    }

    @Override
    public List<CocktailOverviewDto> getMixableCocktails() {
        LOGGER.debug("Get all mixable cocktails");

        List<Cocktail> cocktails = cocktailRepository.findAllByOrderByNameAsc();

        List<CocktailOverviewDto> cocktailOverviewDto = new ArrayList<>();
        for (Cocktail cocktail : cocktails) {
            cocktailOverviewDto.add(cocktailIngredientMapper.cocktailToCocktailOverviewDto(cocktail));
        }
        return cocktailOverviewDto;
    }

}
