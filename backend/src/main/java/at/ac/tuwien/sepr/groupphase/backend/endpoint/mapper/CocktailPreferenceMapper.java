package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.ac.tuwien.sepr.groupphase.backend.entity.Cocktail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CocktailListDto;

@Mapper
public interface CocktailPreferenceMapper {

    List<CocktailListDto> cocktailToCocktailListDto(List<Cocktail> cocktailListDto);

    /*@Mapping(source = "cocktail.id", target = "id")
    @Mapping(source = "cocktail.name", target = "name")
    @Mapping(source = "cocktail.imagePath", target = "imagePath")
    default List<CocktailListDto> cocktailPreferenceToCocktailListDto(List<CocktailPreference> cocktailPreferenceList) {
        Map<String, CocktailListDto> cocktailMap = new HashMap<>();
        for (CocktailPreference eachCocktailPreference : cocktailPreferenceList) {
            String cocktailName = eachCocktailPreference.getCocktail().getName();
            String preferenceName = eachCocktailPreference.getPreference().getName();
            CocktailListDto dto = cocktailMap.computeIfAbsent(cocktailName, key ->
                    new CocktailListDto(eachCocktailPreference.getCocktail().getId(),
                            cocktailName, eachCocktailPreference.getCocktail().getImagePath(), new ArrayList<>(), new ArrayList<>()));
            dto.getPreferenceName().add(preferenceName);
        }
        return new ArrayList<>(cocktailMap.values());
    }*/

}
