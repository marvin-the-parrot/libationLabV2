package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public class IngredientDto {
    private String name;
    private UserListDto users;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserListDto getUsers() {
        return users;
    }

    public void setUsers(UserListDto users) {
        this.users = users;
    }
}
