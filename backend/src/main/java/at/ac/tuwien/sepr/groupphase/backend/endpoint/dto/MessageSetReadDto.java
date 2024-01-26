package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public class MessageSetReadDto {

    private Long id;
    private boolean isRead;

    public MessageSetReadDto() {
    }

    public MessageSetReadDto(Long id, boolean isRead) {
        this.id = id;
        this.isRead = isRead;
    }

    public Long getId() {
        return id;
    }

    public boolean getIsRead() {
        return isRead;
    }
}
