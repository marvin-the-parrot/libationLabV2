package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.util.List;

public class CocktailTagSearchDto {
    String cocktailName;
    List<String> ingredientsName;
    String preferenceName;

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

    public String getPreferenceName() {
        return preferenceName;
    }

    public void setPreferenceName(String preferenceName) {
        this.preferenceName = preferenceName;
    }
}
