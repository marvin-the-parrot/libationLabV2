package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CocktailSerachDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CocktailTagSearchDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Arrays;
import java.util.List;

@Mapper
public interface CocktailTagSearchDtoMapper {


    @Mapping(source = "cocktailName", target = "cocktailName")
    default CocktailTagSearchDto cocktailSearchDtoToCocktailTagSearchDto(CocktailSerachDto cocktailSearchDto) {
        CocktailTagSearchDto searchTagParameters = new CocktailTagSearchDto();
        searchTagParameters.setCocktailName(cocktailSearchDto.getCocktailName());

        if (cocktailSearchDto.getIngredientsName() != null && !cocktailSearchDto.getIngredientsName().isEmpty()) {
            List<String> ingredientsList = Arrays.asList(cocktailSearchDto.getIngredientsName().split(","));
            searchTagParameters.setIngredientsName(ingredientsList);
        }
        if (cocktailSearchDto.getPreferenceName() != null && !cocktailSearchDto.getPreferenceName().isEmpty()) {
            List<String> preferencesList = Arrays.asList(cocktailSearchDto.getPreferenceName().split(","));
            searchTagParameters.setPreferenceName(preferencesList);
        }
        return searchTagParameters;
    }

}
