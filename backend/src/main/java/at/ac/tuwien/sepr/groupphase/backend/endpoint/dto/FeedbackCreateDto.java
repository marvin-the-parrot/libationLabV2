package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

/**
 * DTO to create a feedback.
 */
public class FeedbackCreateDto {

    private Long groupId;
    private Long[] cocktailIds;

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Long[] getCocktailIds() {
        return cocktailIds;
    }

    public void setCocktailIds(Long[] cocktailIds) {
        this.cocktailIds = cocktailIds;
    }
}
