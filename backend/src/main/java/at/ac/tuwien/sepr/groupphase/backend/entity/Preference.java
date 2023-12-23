package at.ac.tuwien.sepr.groupphase.backend.entity;

import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

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

    public Set<CocktailPreference> getCocktailPreference() {
        return cocktailPreference;
    }

    public void setCocktailPreference(Set<CocktailPreference> cocktailPreference) {
        this.cocktailPreference = cocktailPreference;
    }

}
