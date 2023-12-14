package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Column;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity of table Message.
 */
@Entity
public class ApplicationMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private ApplicationUser applicationUser;

    @Column
    private String text;

    @Column(nullable = false)
    private Long groupId;

    @Column(nullable = false)
    private boolean isRead;

    @Column(nullable = false, name = "sent_at")
    private LocalDateTime sentAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ApplicationUser getApplicationUser() {
        return applicationUser;
    }

    public void setApplicationUser(ApplicationUser userId) {
        this.applicationUser = userId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(boolean isRead) {
        this.isRead = isRead;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ApplicationMessage applicationMessage)) {
            return false;
        }
        return Objects.equals(id, applicationMessage.id)
            && Objects.equals(applicationUser, applicationMessage.applicationUser)
            && Objects.equals(text, applicationMessage.text)
            && Objects.equals(groupId, applicationMessage.groupId)
            && Objects.equals(isRead, applicationMessage.isRead)
            && Objects.equals(sentAt, applicationMessage.sentAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, applicationUser, groupId, isRead, sentAt);
    }

    @Override
    public String toString() {
        return "Message{"
            + "id=" + id
            + ", text='" + text + '\''
            + ", sent_at='" + sentAt + '\''
            + '}';
    }

    public static final class ApplicationMessageBuilder {
        private Long id;
        private ApplicationUser applicationUser;
        private String text;
        private Long groupId;
        private boolean isRead;
        private LocalDateTime sentAt;

        private ApplicationMessageBuilder() {
        }

        public static ApplicationMessageBuilder message() {
            return new ApplicationMessageBuilder();
        }

        public ApplicationMessageBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public ApplicationMessageBuilder withApplicationUser(ApplicationUser applicationUser) {
            this.applicationUser = applicationUser;
            return this;
        }

        public ApplicationMessageBuilder withText(String text) {
            this.text = text;
            return this;
        }

        public ApplicationMessageBuilder withGroupId(Long groupId) {
            this.groupId = groupId;
            return this;
        }

        public ApplicationMessageBuilder withIsRead(boolean isRead) {
            this.isRead = isRead;
            return this;
        }

        public ApplicationMessageBuilder withSentAt(LocalDateTime sentAt) {
            this.sentAt = sentAt;
            return this;
        }

        public ApplicationMessage build() {
            ApplicationMessage applicationMessage = new ApplicationMessage();
            applicationMessage.setId(id);
            applicationMessage.setApplicationUser(applicationUser);
            applicationMessage.setText(text);
            applicationMessage.setGroupId(groupId);
            applicationMessage.setIsRead(isRead);
            applicationMessage.setSentAt(sentAt);
            return applicationMessage;
        }
    }
}