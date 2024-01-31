package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

/**
 * DTO to send user groups.
 */
public class UserGroupDto {

    private Long userId;
    private Long groupId;
    private boolean isHost;

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

    public boolean isHost() {
        return isHost;
    }

    public void setHost(boolean host) {
        isHost = host;
    }
}
