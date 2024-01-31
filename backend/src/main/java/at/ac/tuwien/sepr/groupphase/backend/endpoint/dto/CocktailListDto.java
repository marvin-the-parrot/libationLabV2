package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.util.HashMap;
import java.util.List;

/**
 * DTO to send list of cocktails to the frontend to display in menu creation.
 */
public class CocktailListDto {

    private Long id;
    private String name;
    private String imagePath;
    private HashMap<String, String> ingredients;
    private List<String> preferenceName;

    public CocktailListDto(Long id, String name, String imagePath, HashMap<String, String> ingredients, List<String> preferenceName) {
        this.id = id;
        this.name = name;
        this.imagePath = imagePath;
        this.ingredients = ingredients;
        this.preferenceName = preferenceName;
    }

    public CocktailListDto() {
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

    public void setIngredients(HashMap<String, String> ingredients) {
        this.ingredients = ingredients;
    }

    public List<String> getPreferenceName() {
        return preferenceName;
    }

    public void setPreferenceName(List<String> preferenceName) {
        this.preferenceName = preferenceName;
    }

}
