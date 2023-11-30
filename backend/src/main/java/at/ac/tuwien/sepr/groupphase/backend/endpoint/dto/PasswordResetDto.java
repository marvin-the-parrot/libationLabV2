package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public class PasswordResetDto {

    private String email;

    private String password;

    private String token;

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getToken() {
        return token;
    }
}