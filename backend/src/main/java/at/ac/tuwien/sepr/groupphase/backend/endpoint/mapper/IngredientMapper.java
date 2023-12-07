package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.IngredientGroupDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserListDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredient;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper
public interface IngredientMapper {

    @Named("ingredient")
    IngredientGroupDto ingredientToIngredientDto(Ingredient ingredient);

    @IterableMapping(qualifiedByName = "ingredient")
    List<IngredientGroupDto> ingredientToIngredientDto(List<Ingredient> ingredient);

    @Mapping(source = "ingredient.name", target = "name")
    @Mapping(source = "users", target = "users")
    IngredientGroupDto from(Ingredient ingredient, UserListDto[] users);
}
