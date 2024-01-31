package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.CascadeType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToMany;
import jakarta.persistence.ManyToMany;

import java.util.Objects;
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

    @OneToMany(mappedBy = "applicationUser", cascade = CascadeType.REMOVE)
    private Set<UserGroup> userGroups;

    @OneToMany(mappedBy = "applicationUser", cascade = CascadeType.REMOVE)
    private Set<ApplicationMessage> applicationMessages;

    @OneToMany(mappedBy = "applicationUser", cascade = CascadeType.REMOVE)
    private Set<Feedback> feedbacks;

    @ManyToMany
    @JoinTable(
        name = "user_ingredients",
        joinColumns = @JoinColumn(name = "applicationUser_id"),
        inverseJoinColumns = @JoinColumn(name = "ingredient_id"))
    Set<Ingredient> ingredients;

    @ManyToMany
    @JoinTable(
        name = "user_preferences",
        joinColumns = @JoinColumn(name = "applicationUser_id"),
        inverseJoinColumns = @JoinColumn(name = "preference_id"))
    Set<Preference> preferences;

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

    public Set<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(Set<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public Set<Preference> getPreferences() {
        return preferences;
    }

    public void setPreferences(Set<Preference> preferences) {
        this.preferences = preferences;
    }

    public Set<Feedback> getFeedbacks() {
        return feedbacks;
    }

    public void setFeedbacks(Set<Feedback> feedbacks) {
        this.feedbacks = feedbacks;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ApplicationUser applicationUser)) {
            return false;
        }
        return Objects.equals(id, applicationUser.id)
            && Objects.equals(name, applicationUser.name)
            && Objects.equals(email, applicationUser.email)
            && Objects.equals(password, applicationUser.password)
            && Objects.equals(userGroups, applicationUser.userGroups)
            && Objects.equals(applicationMessages, applicationUser.applicationMessages)
            && Objects.equals(ingredients, applicationUser.ingredients)
            && Objects.equals(preferences, applicationUser.preferences)
            && Objects.equals(admin, applicationUser.admin);
    }

    public static final class ApplicationUserBuilder {

        private Long id;
        private String name;
        private String email;
        private String password;
        private Set<Ingredient> ingredients;
        private Set<Preference> preferences;

        private ApplicationUserBuilder() {
        }

        public static ApplicationUserBuilder applicationUser() {
            return new ApplicationUserBuilder();
        }

        public ApplicationUserBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public ApplicationUserBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public ApplicationUserBuilder withEmail(String email) {
            this.email = email;
            return this;
        }

        public ApplicationUserBuilder withPassword(String password) {
            this.password = password;
            return this;
        }

        public ApplicationUserBuilder withIngredients(Set<Ingredient> ingredients) {
            this.ingredients = ingredients;
            return this;
        }

        public ApplicationUserBuilder withPreferences(Set<Preference> preferences) {
            this.preferences = preferences;
            return this;
        }

        public ApplicationUser build() {
            ApplicationUser applicationUser = new ApplicationUser();
            applicationUser.setId(id);
            applicationUser.setName(name);
            applicationUser.setEmail(email);
            applicationUser.setPassword(password);
            applicationUser.setIngredients(ingredients);
            applicationUser.setPreferences(preferences);
            applicationUser.setAdmin(false);
            return applicationUser;
        }
    }
}
