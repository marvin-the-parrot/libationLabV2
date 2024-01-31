package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

/**
 * DTO to send cocktail list displayed in the group overview.
 */
public class CocktailListMenuDto {
    private Long id;
    private String name;
    private FeedbackState rating;

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

    public FeedbackState getRating() {
        return rating;
    }

    public void setRating(FeedbackState rating) {
        this.rating = rating;
    }
}
