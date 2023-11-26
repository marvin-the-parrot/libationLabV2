package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.Set;


/**
 * Entity of table ApplicationUser.
 */
@Entity
public class ApplicationUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "applicationUser")
    private Set<UserGroup> userGroups;

    @OneToMany(mappedBy = "applicationUser")
    private Set<ApplicationMessage> applicationMessages;

    private Boolean admin;

    public ApplicationUser() {
    }

    public ApplicationUser(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.admin = admin;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<UserGroup> getUserGroups() {
        return userGroups;
    }

    public void setUserGroups(Set<UserGroup> userGroups) {
        this.userGroups = userGroups;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<ApplicationMessage> getApplicationMessages() {
        return applicationMessages;
    }

    public void setApplicationMessages(Set<ApplicationMessage> applicationMessages) {
        this.applicationMessages = applicationMessages;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    public static final class ApplicationUserBuilder {

        private Long id;
        private String name;
        private String email;
        private String password;

        private ApplicationUserBuilder() {
        }

        public static ApplicationUser.ApplicationUserBuilder applicationUser() {
            return new ApplicationUser.ApplicationUserBuilder();
        }

        public ApplicationUser.ApplicationUserBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public ApplicationUser.ApplicationUserBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public ApplicationUser.ApplicationUserBuilder withEmail(String email) {
            this.email = email;
            return this;
        }

        public ApplicationUser.ApplicationUserBuilder withPassword(String password) {
            this.password = password;
            return this;
        }

        public ApplicationUser build() {
            ApplicationUser applicationUser = new ApplicationUser();
            applicationUser.setId(id);
            applicationUser.setName(name);
            applicationUser.setEmail(email);
            applicationUser.setPassword(password);
            applicationUser.setAdmin(false);
            return applicationUser;
        }
    }
}
