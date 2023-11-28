package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

import java.util.Set;

/**
 * Entity of table UserGroups.
 */
@Entity
@Table(name = "user_groups")
public class UserGroup {

    @EmbeddedId
    UserGroupKey id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("user_id")
    @JoinColumn(name = "user_id", nullable = false)
    private ApplicationUser applicationUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("group_id")
    @JoinColumn(name = "group_id", nullable = false)
    private ApplicationGroup applicationGroup;

    @Column(name = "is_host")
    private boolean isHost;

    public UserGroupKey getId() {
        return id;
    }

    public void setId(UserGroupKey id) {
        this.id = id;
    }

    public ApplicationUser getUser() {
        return applicationUser;
    }

    public void setUser(ApplicationUser user) {
        this.applicationUser = user;
    }

    public ApplicationGroup getGroups() {
        return applicationGroup;
    }

    public void setGroups(ApplicationGroup applicationGroup) {
        this.applicationGroup = applicationGroup;
    }

    public boolean isHost() {
        return isHost;
    }

    public void setHost(boolean isHost) {
        this.isHost = isHost;
    }

    public static final class UserGroupBuilder {
        private UserGroupKey userGroupKey;
        private ApplicationUser applicationUser;
        private ApplicationGroup applicationGroup;
        private boolean isHost;

        private UserGroupBuilder() {
        }

        public static UserGroupBuilder userGroup() {
            return new UserGroupBuilder();
        }

        public UserGroupBuilder withUserGroupKey(UserGroupKey userGroupKey) {
            this.userGroupKey = userGroupKey;
            return this;
        }

        public UserGroupBuilder withUser(ApplicationUser applicationUser) {
            this.applicationUser = applicationUser;
            return this;
        }

        public UserGroupBuilder withGroup(ApplicationGroup applicationGroup) {
            this.applicationGroup = applicationGroup;
            return this;
        }

        public UserGroupBuilder withIsHost(boolean isHost) {
            this.isHost = isHost;
            return this;
        }


        /**
         * Build application group.
         *
         * @return ApplicationGroup
         */
        public UserGroup build() {
            UserGroup userGroup = new UserGroup();
            userGroup.setId(userGroupKey);
            userGroup.setUser(applicationUser);
            userGroup.setGroups(applicationGroup);
            userGroup.setHost(isHost);
            return userGroup;
        }
    }

}
