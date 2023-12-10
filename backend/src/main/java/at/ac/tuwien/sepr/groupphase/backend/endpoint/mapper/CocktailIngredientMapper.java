package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import java.util.ArrayList;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CocktailListDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.CocktailIngredients;

@Mapper
public interface CocktailIngredientMapper {

    @Mapping(source = "cocktail.id", target = "id")
    @Mapping(source = "cocktail.name", target = "name")
    @Mapping(source = "cocktail.imagePath", target = "imagePath")
    default List<CocktailListDto> cocktailIngredientToCocktailListDto(List<CocktailIngredients> cocktailIngredientsList) {
        List<CocktailListDto> convertedDto = new ArrayList<>();
        for (CocktailIngredients eachCocktailIngredients : cocktailIngredientsList) {
            if (!convertedDto.stream().anyMatch(dto -> dto.getName().equals(eachCocktailIngredients.getCocktail().getName()))) {
                convertedDto.add(new CocktailListDto(eachCocktailIngredients.getCocktail().getId(), 
                      eachCocktailIngredients.getCocktail().getName(), eachCocktailIngredients.getCocktail().getImagePath(), List.of(eachCocktailIngredients.getIngredient().getName())));
            } else {
                convertedDto.stream().anyMatch(dto -> {
                    if (dto.getName().equals(eachCocktailIngredients.getCocktail().getName())) {
                        List<String> ingredientsNameList = new ArrayList<String>();
                        String ingredientsName = eachCocktailIngredients.getIngredient().getName();
                        ingredientsNameList.add(ingredientsName);
                        ingredientsNameList.addAll(dto.getIngredientsName());
                        dto.setIngredientsName(ingredientsNameList);
                    }
                    return true;
                });
            }
        }
        return convertedDto;
    }

    ;
}
