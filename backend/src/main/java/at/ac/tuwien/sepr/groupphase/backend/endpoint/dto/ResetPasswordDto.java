package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

/**
 * DTO to reset a password.
 */
public class ResetPasswordDto {

    private String password;
    private String token;

    public String getPassword() {
        return password;
    }

    public String getToken() {
        return token;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setToken(String token) {
        this.token = token;
    }
}