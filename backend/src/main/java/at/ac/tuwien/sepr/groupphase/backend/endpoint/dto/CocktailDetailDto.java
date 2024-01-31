package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.util.HashMap;
import java.util.List;

/**
 * DTO for cocktail details to display instructions and ingredients.
 */
public class CocktailDetailDto {

    private Long id;
    private String name;
    private String imagePath;

    private HashMap<String, String> ingredients;
    private List<String> preferenceName;
    private String instructions;

    public CocktailDetailDto() {
    }

    public CocktailDetailDto(Long id, String name, String imagePath, HashMap<String, String> ingredients, List<String> preferenceName, String instructions) {
        this.id = id;
        this.name = name;
        this.imagePath = imagePath;
        this.ingredients = ingredients;
        this.preferenceName = preferenceName;
        this.instructions = instructions;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public HashMap<String, String> getIngredients() {
        return ingredients;
    }

    public List<String> getPreferenceName() {
        return preferenceName;
    }

    public String getInstructions() {
        return instructions;
    }

}
