package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;

import java.util.Set;

/**
 * Entity of table UserGroups.
 */
@Entity
public class ApplicationGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "applicationGroup")
    private Set<UserGroup> userGroups;

    @OneToMany(mappedBy = "applicationGroup", cascade = CascadeType.REMOVE)
    private Set<Feedback> feedbacks;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(
        name = "menu",
        joinColumns = @JoinColumn(name = "application_group_id"),
        inverseJoinColumns = @JoinColumn(name = "cocktail_id"))
    private Set<Cocktail> cocktails;

    public Set<UserGroup> getUserGroups() {
        return userGroups;
    }

    public void setUserGroups(Set<UserGroup> userGroups) {
        this.userGroups = userGroups;
    }

    public Set<Cocktail> getCocktails() {
        return cocktails;
    }

    public void setCocktails(Set<Cocktail> cocktails) {
        this.cocktails = cocktails;
    }

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

    public Set<UserGroup> getMembers() {
        return userGroups;
    }

    public void setMembers(Set<UserGroup> userGroups) {
        this.userGroups = userGroups;
    }

    public Set<Feedback> getFeedbacks() {
        return feedbacks;
    }

    public void setFeedbacks(Set<Feedback> feedbacks) {
        this.feedbacks = feedbacks;
    }

    /**
     * Group builder.
     */
    public static final class GroupBuilder {
        private Long id;
        private String name;
        private Set<UserGroup> groupUsers;

        private GroupBuilder() {
        }

        public static GroupBuilder group() {
            return new GroupBuilder();
        }

        public GroupBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public GroupBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public GroupBuilder withMembers(Set<UserGroup> userGroups) {
            this.groupUsers = userGroups;
            return this;
        }

        /**
         * Build application group.
         *
         * @return ApplicationGroup
         */
        public ApplicationGroup build() {
            ApplicationGroup applicationGroup = new ApplicationGroup();
            applicationGroup.setId(id);
            applicationGroup.setName(name);
            applicationGroup.setMembers(groupUsers);
            return applicationGroup;
        }
    }

}
