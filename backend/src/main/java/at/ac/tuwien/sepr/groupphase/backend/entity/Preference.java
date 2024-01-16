package at.ac.tuwien.sepr.groupphase.backend.entity;

import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.CascadeType;
import jakarta.persistence.ManyToMany;

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
    private Set<ApplicationUser> applicationUser;

    @ManyToMany(cascade = CascadeType.REMOVE)
    private Set<Cocktail> cocktails;

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

    public Set<Cocktail> getCocktails() {
        return cocktails;
    }

    public void setCocktails(Set<Cocktail> cocktails) {
        this.cocktails = cocktails;
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
