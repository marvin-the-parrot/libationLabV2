package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

/**
 * DTO to search for existing groups.
 */
public class UserSearchExistingGroupDto {

    private String name;
    private Long groupId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }
}
