package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

/**
 * DTO for details of a message for front-end display.
 */
public class MessageDetailDto {

    private Long id;
    private String text;
    private GroupDetailDto group;
    private boolean isRead;
    private String sentAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public GroupDetailDto getGroup() {
        return group;
    }

    public void setGroup(GroupDetailDto group) {
        this.group = group;
    }

    public boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(boolean isRead) {
        this.isRead = isRead;
    }

    public String getSentAt() {
        return sentAt;
    }

    public void setSentAt(String sentAt) {
        this.sentAt = sentAt;
    }
}
