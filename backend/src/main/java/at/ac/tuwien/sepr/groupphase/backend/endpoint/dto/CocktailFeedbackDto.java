package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public class CocktailFeedbackDto {

    private Long cocktailId;
    private Long groupId;
    private String rating;

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

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}
