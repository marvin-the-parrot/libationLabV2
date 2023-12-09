package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CocktailListDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.CocktailIngredients;

@Mapper
public interface CocktailIngredientMapper {

    List<CocktailListDto> cocktailIngredientToCocktailListDto(List<CocktailIngredients> cocktailIngredientsList);
}
