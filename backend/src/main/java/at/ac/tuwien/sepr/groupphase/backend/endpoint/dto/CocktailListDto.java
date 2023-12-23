package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.util.List;

public class CocktailListDto {

    private Long id;
    private String name;
    private String imagePath;
    private List<String> ingredientsName;
    private List<String> preferenceName;

    public CocktailListDto() {
    }

    public CocktailListDto(Long id, String name, String imagePath, List<String> ingredientsName, List<String> preferenceName) {
        this.id = id;
        this.name = name;
        this.imagePath = imagePath;
        this.ingredientsName = ingredientsName;
        this.preferenceName = preferenceName;
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
