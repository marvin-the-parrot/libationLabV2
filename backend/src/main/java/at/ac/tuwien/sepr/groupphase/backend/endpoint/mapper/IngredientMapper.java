package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.IngredientGroupDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.IngredientListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserListDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper
public interface IngredientMapper {

    @Mapping(source = "ingredient.name", target = "name")
    @Mapping(source = "users", target = "users")
    IngredientGroupDto from(Ingredient ingredient, UserListDto[] users);

    IngredientListDto ingredientToIngredientListDto(Ingredient ingredient);

    List<IngredientListDto> ingredientToIngredientListDto(List<Ingredient> ingredient);

    Ingredient ingredientListDtoToIngredient(IngredientListDto ingredientListDto);

    List<Ingredient> ingredientListDtoToIngredient(IngredientListDto[] ingredientListDto);
}
