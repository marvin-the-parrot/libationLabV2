package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

/**
 * DTO to send information about the number of messages a user has.
 */
public class MessageCountDto {
    private Long count;

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
