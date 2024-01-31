package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

/**
 * DTO to create a group.
 */
public class GroupCreateDto {
    private long id;
    private String name;
    private UserListDto host;
    private String[] cocktails;
    private UserListDto[] members;

    public GroupCreateDto() {
    }

    public GroupCreateDto(long id, String name, UserListDto host, String[] cocktails, UserListDto[] members) {
        this.id = id;
        this.name = name;
        this.host = host;
        this.cocktails = cocktails;
        this.members = members;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setHost(UserListDto host) {
        this.host = host;
    }

    public void setCocktails(String[] cocktails) {
        this.cocktails = cocktails;
    }

    public void setMembers(UserListDto[] members) {
        this.members = members;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public UserListDto getHost() {
        return host;
    }

    public String[] getCocktails() {
        return cocktails;
    }

    public UserListDto[] getMembers() {
        return members;
    }
}
