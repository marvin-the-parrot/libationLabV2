package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

/**
 * DTO for user lists in groups.
 */
public class UserListGroupDto {
    public UserListGroupDto(Long id, String name, boolean isHost) {
        this.id = id;
        this.name = name;
        this.isHost = isHost;
    }

    private Long id;
    private String name;
    private boolean isHost;

    public UserListGroupDto() {

    }

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

    public boolean isHost() {
        return isHost;
    }

    public void setHost(boolean host) {
        isHost = host;
    }
}
