package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class UserGroupKey implements Serializable {

    @Column(name = "user_id")
    Long userId;

    @Column(name = "group_id")
    Long groupId;
}
