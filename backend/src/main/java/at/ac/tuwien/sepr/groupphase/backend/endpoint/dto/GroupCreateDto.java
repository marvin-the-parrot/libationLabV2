package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;


public class GroupCreateDto {
    private long id;
    private String name;
    private UserListDto host;
    private String[] cocktails;
    private UserListDto[] members;

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
