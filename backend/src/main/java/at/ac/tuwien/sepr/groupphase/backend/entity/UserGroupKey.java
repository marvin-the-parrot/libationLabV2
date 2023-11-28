package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.*;

import java.io.Serializable;

/**
 * Embedded Id of entity UserGroup.
 */
@Embeddable
public class UserGroupKey implements Serializable {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private ApplicationUser user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private ApplicationGroup group;

    public UserGroupKey(ApplicationUser user, ApplicationGroup group) {
        this.user = user;
        this.group = group;
    }

    public UserGroupKey() {

    }
}
