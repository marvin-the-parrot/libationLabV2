package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "reset_tokens")
public class ResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "token")
    private String token;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Builder method

    public static class ResetTokenBuilder {
        private Long userId;
        private String token;

        private ResetTokenBuilder() {
        }

        public static ResetTokenBuilder resetToken() {
            return new ResetTokenBuilder();
        }

        public ResetTokenBuilder withUserId(Long userId) {
            this.userId = userId;
            return this;
        }

        public ResetTokenBuilder withToken(String token) {
            this.token = token;
            return this;
        }

        public ResetToken build() {
            ResetToken resetToken = new ResetToken();
            resetToken.setUserId(this.userId);
            resetToken.setToken(this.token);
            resetToken.setCreatedAt(LocalDateTime.now());
            return resetToken;
        }
    }

}
