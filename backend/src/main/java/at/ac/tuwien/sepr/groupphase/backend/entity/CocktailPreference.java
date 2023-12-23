package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

/**
 * Entity of table CocktailPreference.
 */
@Entity
@Table(name = "cocktail_preference")
public class CocktailPreference {

    @EmbeddedId
    private CocktailPreferenceKey cocktailPreferenceKey;

    @ManyToOne
    @MapsId("cocktailId")
    @JoinColumn(name = "cocktail_id")
    private Cocktail cocktail;

    @ManyToOne
    @MapsId("preferenceId")
    @JoinColumn(name = "preference_id")
    private Preference preference;

    public CocktailPreferenceKey getCocktailPreferenceKey() {
        return cocktailPreferenceKey;
    }

    public void setCocktailPreferenceKey(CocktailPreferenceKey cocktailPreferenceKey) {
        this.cocktailPreferenceKey = cocktailPreferenceKey;
    }

    public Cocktail getCocktail() {
        return cocktail;
    }

    public void setCocktail(Cocktail cocktail) {
        this.cocktail = cocktail;
    }

    public Preference getPreference() {
        return preference;
    }

    public void setPreference(Preference preference) {
        this.preference = preference;
    }

}
