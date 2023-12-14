package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CocktailListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CocktailOverviewDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Cocktail;
import at.ac.tuwien.sepr.groupphase.backend.entity.CocktailIngredients;

@Mapper
public interface CocktailIngredientMapper {

    @Mapping(source = "cocktail.id", target = "id")
    @Mapping(source = "cocktail.name", target = "name")
    @Mapping(source = "cocktail.imagePath", target = "imagePath")
    default List<CocktailListDto> cocktailIngredientToCocktailListDto(List<CocktailIngredients> cocktailIngredientsList) {
        Map<String, CocktailListDto> cocktailMap = new HashMap<>();
        for (CocktailIngredients eachCocktailIngredients : cocktailIngredientsList) {
            String cocktailName = eachCocktailIngredients.getCocktail().getName();
            String ingredientName = eachCocktailIngredients.getIngredient().getName();
            CocktailListDto dto = cocktailMap.computeIfAbsent(cocktailName, key ->
                    new CocktailListDto(eachCocktailIngredients.getCocktail().getId(),
                            cocktailName, eachCocktailIngredients.getCocktail().getImagePath(), new ArrayList<>()));
            dto.getIngredientsName().add(ingredientName);
        }
        return new ArrayList<>(cocktailMap.values());
    }

    
    CocktailOverviewDto cocktailToCocktailOverviewDto(Cocktail cocktail);
}
