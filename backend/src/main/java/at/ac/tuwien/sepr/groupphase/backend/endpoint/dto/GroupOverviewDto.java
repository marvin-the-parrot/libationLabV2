package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

/**
 * DTO to send group detail data to the frontend.
 */
public class GroupOverviewDto {

    private Long id;
    private String name;
    private CocktailOverviewDto[] cocktails;
    private UserListGroupDto host;

    public GroupOverviewDto(Long id, String name, CocktailOverviewDto[] cocktails, UserListGroupDto host, UserListGroupDto[] members) {
        this.id = id;
        this.name = name;
        this.cocktails = cocktails;
        this.host = host;
        this.members = members;
    }

    // Default constructor
    public GroupOverviewDto() {
    }

    public UserListGroupDto getHost() {
        return host;
    }

    public void setHost(UserListGroupDto host) {
        this.host = host;
    }

    private UserListGroupDto[] members;

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

    public CocktailOverviewDto[] getCocktails() {
        return cocktails;
    }

    public void setCocktails(CocktailOverviewDto[] cocktail) {
        this.cocktails = cocktail;
    }

    public UserListGroupDto[] getMembers() {
        return members;
    }

    public void setMembers(UserListGroupDto[] members) {
        this.members = members;
    }
}
