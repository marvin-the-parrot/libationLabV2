package at.ac.tuwien.sepr.groupphase.backend.entity;

import java.util.Set;

import jakarta.persistence.*;

/**
 * Entity of table Preference.
 */
@Entity
public class Preference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToMany(mappedBy = "preferences")
    Set<ApplicationUser> applicationUser;

    @OneToMany(mappedBy = "preference", cascade = CascadeType.REMOVE)
    private Set<CocktailPreference> cocktailPreference;

    public Preference() {
    }

    public Preference(Long id, String name) {
        this.id = id;
        this.name = name;
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
    public Set<ApplicationUser> getApplicationUser() {
        return applicationUser;
    }
    public void setApplicationUser(Set<ApplicationUser> applicationUser) {
        this.applicationUser = applicationUser;
    }

    public Set<CocktailPreference> getCocktailPreference() {
        return cocktailPreference;
    }

    public void setCocktailPreference(Set<CocktailPreference> cocktailPreference) {
        this.cocktailPreference = cocktailPreference;
    }

    public static final class PreferencesBuilder {
        private Long id;
        private String name;
        private Set<ApplicationUser> applicationUser;
        private PreferencesBuilder() {
        }
        public static Preference.PreferencesBuilder preferences() {
            return new Preference.PreferencesBuilder();
        }
        public Preference.PreferencesBuilder withId(Long id) {
            this.id = id;
            return this;
        }
        public Preference.PreferencesBuilder withName(String name) {
            this.name = name;
            return this;
        }
        public Preference.PreferencesBuilder withApplicationUsers(Set<ApplicationUser> applicationUser) {
            this.applicationUser = applicationUser;
            return this;
        }
        public Preference build() {
            Preference preference = new Preference();
            preference.setId(id);
            preference.setName(name);
            preference.setApplicationUser(applicationUser);
            return preference;
        }
    }

}
