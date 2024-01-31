package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

/**
 * DTO to search for cocktials according to names, ingredients and preferences.
 */
public class CocktailSerachDto {

    private String  cocktailName;
    private String ingredientsName;
    private String preferenceName;

    public String getCocktailName() {
        return cocktailName;
    }

    public void setCocktailName(String cocktailName) {
        this.cocktailName = cocktailName;
    }

    public String getIngredientsName() {
        return ingredientsName;
    }

    public void setIngredientsName(String ingredientsName) {
        this.ingredientsName = ingredientsName;
    }

    public String getPreferenceName() {
        return preferenceName;
    }

    public void setPreferenceName(String preferenceName) {
        this.preferenceName = preferenceName;
    }
}
