package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

/**
 * DTO to send information for a cocktail card including an image.
 */
public class CocktailOverviewDto {

    private Long id;
    private String name;
    private String imagePath;

    public CocktailOverviewDto() {
    }

    public CocktailOverviewDto(Long id, String name, String imagePath) {
        this.id = id;
        this.name = name;
        this.imagePath = imagePath;
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
}
