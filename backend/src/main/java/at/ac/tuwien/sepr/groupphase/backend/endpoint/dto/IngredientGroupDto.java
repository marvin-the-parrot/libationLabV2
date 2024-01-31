package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

/**
 * DTO to send information about ingredients a group has.
 */
public class IngredientGroupDto {
    private String name;
    private UserListDto[] users;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserListDto[] getUsers() {
        return users;
    }

    public void setUsers(UserListDto[] users) {
        this.users = users;
    }
}
