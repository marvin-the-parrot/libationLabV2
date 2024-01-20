package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.util.List;

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
