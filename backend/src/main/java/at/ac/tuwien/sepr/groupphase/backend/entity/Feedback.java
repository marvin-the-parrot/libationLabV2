package at.ac.tuwien.sepr.groupphase.backend.entity;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.FeedbackState;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "recommendation")
public class Feedback {

    @EmbeddedId
    private FeedbackKey feedbackKey;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private ApplicationUser applicationUser;

    @ManyToOne
    @MapsId("groupId")
    @JoinColumn(name = "group_id")
    private ApplicationGroup applicationGroup;

    @ManyToOne
    @MapsId("cocktailId")
    @JoinColumn(name = "cocktail_id")
    private Cocktail cocktail;

    @Column(nullable = false)
    private FeedbackState rating;

    public FeedbackKey getFeedbackKey() {
        return feedbackKey;
    }

    public void setFeedbackKey(FeedbackKey feedbackKey) {
        this.feedbackKey = feedbackKey;
    }

    public ApplicationUser getApplicationUser() {
        return applicationUser;
    }

    public void setApplicationUser(ApplicationUser applicationUser) {
        this.applicationUser = applicationUser;
    }

    public ApplicationGroup getApplicationGroup() {
        return applicationGroup;
    }

    public void setApplicationGroup(ApplicationGroup applicationGroup) {
        this.applicationGroup = applicationGroup;
    }

    public Cocktail getCocktail() {
        return cocktail;
    }

    public void setCocktail(Cocktail cocktail) {
        this.cocktail = cocktail;
    }

    public FeedbackState getRating() {
        return rating;
    }

    public void setRating(FeedbackState rating) {
        this.rating = rating;
    }
}
