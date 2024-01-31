package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

/**
 * DTO to store preference lists.
 */
public class PreferenceListDto {
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
}
