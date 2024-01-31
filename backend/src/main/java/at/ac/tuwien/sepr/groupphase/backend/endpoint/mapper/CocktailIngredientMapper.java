package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CocktailDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CocktailListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CocktailOverviewDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Cocktail;
import at.ac.tuwien.sepr.groupphase.backend.entity.CocktailIngredients;
import at.ac.tuwien.sepr.groupphase.backend.entity.Preference;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface CocktailIngredientMapper {

    @Mapping(source = "cocktail.id", target = "id")
    @Mapping(source = "cocktail.name", target = "name")
    @Mapping(source = "cocktail.imagePath", target = "imagePath")
    default List<CocktailListDto> cocktailIngredientToCocktailListDto(List<Cocktail> cocktails) {
        Map<String, CocktailListDto> cocktailMap = new HashMap<>();
        for (Cocktail cocktail : cocktails) {
            String cocktailName = cocktail.getName();
            HashMap<String, String> ingredients = new HashMap<>();
            List<String> preferenceName = new ArrayList<>();
            for (Preference preference : cocktail.getPreferences()) {
                preferenceName.add(preference.getName());
            }
            for (CocktailIngredients cocktailIngredient : cocktail.getCocktailIngredients()) {
                ingredients.put(cocktailIngredient.getIngredient().getName(), cocktailIngredient.getQuantity());
            }
            CocktailListDto dto = cocktailMap.computeIfAbsent(cocktailName,
                key -> new CocktailListDto(cocktail.getId(), cocktailName, cocktail.getImagePath(), new HashMap<>(), new ArrayList<>()));
            dto.setIngredients(ingredients);
            dto.setPreferenceName(preferenceName);
        }
        List<CocktailListDto> result = new ArrayList<>(cocktailMap.values());

        return result;
    }

    CocktailOverviewDto cocktailToCocktailOverviewDto(Cocktail cocktail);

    List<CocktailOverviewDto> cocktailToCocktailOverviewDtoList(List<Cocktail> cocktail);

    List<Cocktail> cocktailOverviewDtoToCocktailList(List<CocktailOverviewDto> cocktail);


    @Mapping(source = "cocktail.id", target = "id")
    @Mapping(source = "cocktail.name", target = "name")
    @Mapping(source = "cocktail.imagePath", target = "imagePath")
    default CocktailDetailDto cocktailToCocktailDetailDto(Cocktail cocktail) {
        HashMap<String, String> ingredients = new HashMap<>(); // <ingredientName, quantity>
        for (var ingredient : cocktail.getCocktailIngredients()) {
            ingredients.put(ingredient.getIngredient().getName(), ingredient.getQuantity());
        }
        List<String> preferenceName = new ArrayList<>();
        for (Preference preference : cocktail.getPreferences()) {
            preferenceName.add(preference.getName());
        }
        return new CocktailDetailDto(cocktail.getId(), cocktail.getName(), cocktail.getImagePath(), ingredients, preferenceName, cocktail.getInstructions());
    }
}
