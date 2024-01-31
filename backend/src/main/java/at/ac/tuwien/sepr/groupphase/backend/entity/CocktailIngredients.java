package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

/**
 * Entity of table CocktailIngredients.
 */
@Entity
@Table(name = "cocktail_ingredients")
public class CocktailIngredients {

    @EmbeddedId
    private CocktailIngredientsKey cocktailIngredientsKey;

    @ManyToOne
    @MapsId("cocktailId")
    @JoinColumn(name = "cocktail_id")
    private Cocktail cocktail;

    @ManyToOne
    @MapsId("ingredientId")
    @JoinColumn(name = "ingredient_id")
    private Ingredient ingredient;

    private String quantity;

    public CocktailIngredientsKey getCocktailIngredientsKey() {
        return cocktailIngredientsKey;
    }

    public void setCocktailIngredientsKey(CocktailIngredientsKey cocktailIngredientsKey) {
        this.cocktailIngredientsKey = cocktailIngredientsKey;
    }

    public Cocktail getCocktail() {
        return cocktail;
    }

    public void setCocktail(Cocktail cocktail) {
        this.cocktail = cocktail;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
