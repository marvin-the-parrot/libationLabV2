package at.ac.tuwien.sepr.groupphase.backend.entity;

import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Column;


@Entity
public class ApplicationGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "applicationGroup")
    private Set<UserGroup> userGroups;

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

    public static final class GroupBuilder {
        private Long id;
        private String name;
        private Set<UserGroup> groupUsers;

        private GroupBuilder() {
        }

        public static GroupBuilder aGroup() {
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

        public ApplicationGroup build() {
            ApplicationGroup applicationGroup = new ApplicationGroup();
            applicationGroup.setId(id);
            applicationGroup.setName(name);
            applicationGroup.setMembers(groupUsers);
            return applicationGroup;
        }
    }

}
