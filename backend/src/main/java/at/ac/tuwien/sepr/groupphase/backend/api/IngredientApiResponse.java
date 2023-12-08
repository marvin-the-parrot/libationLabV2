package at.ac.tuwien.sepr.groupphase.backend.api;

import java.util.List;

public class IngredientApiResponse {
    private List<IngredientApi> ingredients;

    public List<IngredientApi> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<IngredientApi> ingredients) {
        this.ingredients = ingredients;
    }
}
