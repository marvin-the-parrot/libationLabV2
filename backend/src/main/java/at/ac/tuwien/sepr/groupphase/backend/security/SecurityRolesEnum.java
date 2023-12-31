package at.ac.tuwien.sepr.groupphase.backend.security;

public enum SecurityRolesEnum {
    ROLE_ADMIN(Roles.ROLE_ADMIN),
    ROLE_USER(Roles.ROLE_USER);

    public static class Roles {
        public static final String ROLE_USER = "ROLE_USER";
        public static final String ROLE_ADMIN = "ROLE_ADMIN";
    }

    private final String role;

    SecurityRolesEnum(String role) {
        this.role = role;
    }

    public String toString() {
        return this.role;
    }
}
