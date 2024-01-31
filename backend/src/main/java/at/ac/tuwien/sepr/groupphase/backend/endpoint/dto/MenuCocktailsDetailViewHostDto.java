package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

/**
 * DTO for cocktails in a menu when the user is a host.
 */
public class MenuCocktailsDetailViewHostDto {
    private Long groupId;
    private CocktailFeedbackHostDto[] cocktailsList;

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public CocktailFeedbackHostDto[] getCocktailsList() {
        return cocktailsList;
    }

    public void setCocktailsList(CocktailFeedbackHostDto[] cocktailsList) {
        this.cocktailsList = cocktailsList;
    }
}
