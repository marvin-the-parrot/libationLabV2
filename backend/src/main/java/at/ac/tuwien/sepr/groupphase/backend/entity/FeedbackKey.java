package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Column;

import java.io.Serializable;

public class FeedbackKey implements Serializable {

    @Column(name = "user_id")
    public Long user;

    @Column(name = "group_id")
    private Long group;

    @Column(name = "cocktail_id")
    private Long cocktail;

    public FeedbackKey(Long user, Long group, Long cocktail) {
        this.user = user;
        this.group = group;
        this.cocktail = cocktail;
    }

    public FeedbackKey() {

    }
}
