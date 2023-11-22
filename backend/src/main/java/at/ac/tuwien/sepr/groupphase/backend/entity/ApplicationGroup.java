package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.Set;

/**
 * Entity of table Group.
 *
 */
@Entity
public class ApplicationGroup {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @OneToMany(mappedBy = "id.group")
  private Set<UserGroup> groupUsers;

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
    return groupUsers;
  }

  public void setMembers(Set<UserGroup> userGroups) {
    this.groupUsers = userGroups;
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

    public Group build() {
      Group group = new Group();
      group.setId(id);
      group.setName(name);
      group.setMembers(groupUsers); 
      return group;
    }
  }

}
