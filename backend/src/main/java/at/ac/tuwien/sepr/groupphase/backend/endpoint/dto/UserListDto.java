package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.util.Objects;

/**
 * DTO to store users.
 */
public class UserListDto {
    private Long id;
    private String name;

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

    @Override
    public boolean equals(Object o) { // Needed for testing
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserListDto that = (UserListDto) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name);
    }
}
