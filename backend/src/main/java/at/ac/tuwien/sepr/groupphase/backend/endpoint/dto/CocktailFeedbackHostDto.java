package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

/**
 * DTO to send cocktail feedback to the frontend for the host to see.
 */
public class CocktailFeedbackHostDto {

    private Long id;
    private String name;
    private int positiveRating;
    private int negativeRating;

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

    public int getPositiveRating() {
        return positiveRating;
    }

    public void setPositiveRating(int positiveRating) {
        this.positiveRating = positiveRating;
    }

    public int getNegativeRating() {
        return negativeRating;
    }

    public void setNegativeRating(int negativeRating) {
        this.negativeRating = negativeRating;
    }
}
