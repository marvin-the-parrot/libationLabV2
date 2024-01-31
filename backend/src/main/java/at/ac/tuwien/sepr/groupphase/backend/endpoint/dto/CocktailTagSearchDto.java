package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.util.List;

/**
 * DTO to search for a according to names, ingredients and preferences using tags.
 */
public class CocktailTagSearchDto {
    String cocktailName;
    List<String> ingredientsName;
    List<String> preferenceName;

    public String getCocktailName() {
        return cocktailName;
    }

    public void setCocktailName(String cocktailName) {
        this.cocktailName = cocktailName;
    }

    public List<String> getIngredientsName() {
        return ingredientsName;
    }

    public void setIngredientsName(List<String> ingredientsName) {
        this.ingredientsName = ingredientsName;
    }

    public List<String> getPreferenceName() {
        return preferenceName;
    }

    public void setPreferenceName(List<String> preferenceName) {
        this.preferenceName = preferenceName;
    }
}
