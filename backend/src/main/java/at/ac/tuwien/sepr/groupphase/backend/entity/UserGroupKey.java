package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

/**
 * Embedded Id of entity UserGroup.
 */
@Embeddable
public class UserGroupKey implements Serializable {

    @Column(name = "user_id")
    public Long user;

    @Column(name = "group_id")
    private Long group;

    public UserGroupKey(Long user, Long group) {
        this.user = user;
        this.group = group;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UserGroupKey that = (UserGroupKey) o;

        if (!user.equals(that.user)) {
            return false;
        }
        return group.equals(that.group);
    }

    @Override
    public int hashCode() {
        int result = user.hashCode();
        result = 31 * result + group.hashCode();
        return result;
    }

    public UserGroupKey() {

    }
}
