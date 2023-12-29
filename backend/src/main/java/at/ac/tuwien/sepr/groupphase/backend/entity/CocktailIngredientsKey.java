package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

/**
 * Embedded Id of entity CocktailIngredients.
 */
@Embeddable
public class CocktailIngredientsKey implements Serializable {

    @Column(name = "cocktail_id")
    public Long cocktail;

    @Column(name = "ingredient_id")
    private Long ingredient;

    public CocktailIngredientsKey(Long cocktail, Long ingredient) {
        this.cocktail = cocktail;
        this.ingredient = ingredient;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CocktailIngredientsKey that = (CocktailIngredientsKey) o;

        if (!cocktail.equals(that.cocktail)) {
            return false;
        }
        return ingredient.equals(that.ingredient);
    }

    @Override
    public int hashCode() {
        int result = cocktail.hashCode();
        result = 31 * result + ingredient.hashCode();
        return result;
    }

    public CocktailIngredientsKey() {

    }
}
