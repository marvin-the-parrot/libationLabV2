package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public class MessageSetReadDto {

    private Long id;
    private boolean isRead;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean getIsRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
}
