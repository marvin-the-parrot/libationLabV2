package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import java.util.List;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CocktailOverviewDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Cocktail;
import org.mapstruct.Mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CocktailListDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.CocktailIngredients;

@Mapper
public interface CocktailIngredientMapper {

    List<CocktailListDto> cocktailIngredientToCocktailListDto(List<CocktailIngredients> cocktailIngredientsList);

    CocktailOverviewDto cocktailToCocktailOverviewDto(Cocktail cocktail);
}
