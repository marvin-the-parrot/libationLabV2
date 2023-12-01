package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import org.springframework.web.bind.annotation.GetMapping;

public class UserListGroupDto {
    public UserListGroupDto(Long id, String name, boolean isHost) {
        this.id = id;
        this.name = name;
        this.isHost = isHost;
    }

    private Long id;
    private String name;
    private boolean isHost;

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
