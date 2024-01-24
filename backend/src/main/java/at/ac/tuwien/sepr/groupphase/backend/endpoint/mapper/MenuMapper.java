package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CocktailFeedbackHostDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MenuCocktailsDetailViewHostDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Cocktail;
import org.mapstruct.Mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Mapper
public interface MenuMapper {

    default MenuCocktailsDetailViewHostDto cocktailFeedbackToCocktailFeedbackHostDto(HashMap<Cocktail, int[]> cocktailRatings, Long groupId) {
        List<CocktailFeedbackHostDto> cocktailFeedbackHostDtoList = new ArrayList<>();

        for (Cocktail cocktail : cocktailRatings.keySet()) {
            CocktailFeedbackHostDto cocktailFeedbackHostDto = new CocktailFeedbackHostDto();
            cocktailFeedbackHostDto.setId(cocktail.getId());
            cocktailFeedbackHostDto.setName(cocktail.getName());
            cocktailFeedbackHostDto.setPositiveRating(cocktailRatings.get(cocktail)[0]);
            cocktailFeedbackHostDto.setNegativeRating(cocktailRatings.get(cocktail)[1]);
            cocktailFeedbackHostDtoList.add(cocktailFeedbackHostDto);
        }

        MenuCocktailsDetailViewHostDto menuCocktailsDetailViewHostDto = new MenuCocktailsDetailViewHostDto();
        menuCocktailsDetailViewHostDto.setGroupId(groupId);
        menuCocktailsDetailViewHostDto.setCocktailsList(cocktailFeedbackHostDtoList.toArray(new CocktailFeedbackHostDto[0]));

        return menuCocktailsDetailViewHostDto;
    }
}
