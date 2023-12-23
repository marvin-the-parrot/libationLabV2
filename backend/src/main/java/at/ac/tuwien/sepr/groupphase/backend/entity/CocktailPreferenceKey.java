package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * Embedded Id of entity CocktailPreferenceKey.
 */
@Embeddable
public class CocktailPreferenceKey {

    @Column(name = "cocktail_id")
    public Long cocktail;

    @Column(name = "preference_id")
    private Long preference;

    public CocktailPreferenceKey(Long cocktail, Long preference) {
        this.cocktail = cocktail;
        this.preference = preference;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CocktailPreferenceKey that = (CocktailPreferenceKey) o;

        if (!cocktail.equals(that.cocktail)) {
            return false;
        }
        return preference.equals(that.preference);
    }

    @Override
    public int hashCode() {
        int result = cocktail.hashCode();
        result = 31 * result + preference.hashCode();
        return result;
    }

    public CocktailPreferenceKey() {

    }
}
