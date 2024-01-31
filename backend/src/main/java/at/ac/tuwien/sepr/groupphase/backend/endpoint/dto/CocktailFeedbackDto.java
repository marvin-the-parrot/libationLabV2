package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

/**
 * DTO to send information if the current user has given feedback yet.
 */
public class CocktailFeedbackDto {

    private Long cocktailId;
    private Long groupId;
    private FeedbackState rating;

    public Long getCocktailId() {
        return cocktailId;
    }

    public void setCocktailId(Long cocktailId) {
        this.cocktailId = cocktailId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public FeedbackState getRating() {
        return rating;
    }

    public void setRating(FeedbackState rating) {
        this.rating = rating;
    }
}
