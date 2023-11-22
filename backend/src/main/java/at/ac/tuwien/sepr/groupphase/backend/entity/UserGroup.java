package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Entity of table UserGroups.
 *
 */
@Entity
@Table(name = "user_groups")
public class UserGroup {

  @EmbeddedId
  UserGroupKey id;

  @Column(name = "is_host")
  private boolean isHost;

  public boolean isHost() {
    return isHost;
  }

  public void setHost(boolean isHost) {
    this.isHost = isHost;
  }

}
