package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

/**
 * DTO for cocktails in a menu.
 */
public class MenuCocktailsDetailViewDto {
    private Long groupId;
    private CocktailListMenuDto[] cocktailsList;

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public CocktailListMenuDto[] getCocktailsList() {
        return cocktailsList;
    }

    public void setCocktailsList(CocktailListMenuDto[] cocktailsList) {
        this.cocktailsList = cocktailsList;
    }
}
