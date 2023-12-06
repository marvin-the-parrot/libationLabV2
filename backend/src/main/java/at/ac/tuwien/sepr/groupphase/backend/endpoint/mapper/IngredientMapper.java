package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.IngredientDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredient;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.List;

@Mapper
public interface IngredientMapper {

    @Named("ingredient")
    IngredientDto ingredientToIngredientDto(Ingredient ingredient);

    @IterableMapping(qualifiedByName = "ingredient")
    List<IngredientDto> ingredientToIngredientDto(List<Ingredient> ingredient);
}
