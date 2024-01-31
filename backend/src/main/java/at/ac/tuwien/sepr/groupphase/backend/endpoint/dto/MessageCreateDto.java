package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

/**
 * DTO to create a message.
 */
public class MessageCreateDto {

    private Long userId;

    private Long groupId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }
}
