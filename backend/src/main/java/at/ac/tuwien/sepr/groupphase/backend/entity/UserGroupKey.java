package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.io.Serializable;

/**
 * Embedded Id of entity UserGroup.
 *
 */
@Embeddable
public class UserGroupKey implements Serializable {

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "group_id")
  private Group group;
}
