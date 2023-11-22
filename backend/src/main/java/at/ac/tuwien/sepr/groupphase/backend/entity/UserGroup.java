package at.ac.tuwien.sepr.groupphase.backend.entity;


import jakarta.persistence.*;


//TODO: replace this class with a correct ApplicationUser Entity implementation
@Entity
@Table(name = "user_groups")
public class UserGroup {

    @EmbeddedId
    UserGroupKey id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("user_id")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("group_id")
    @JoinColumn(name = "group_id", nullable = false)
    private Group groups;

    @Column(name = "is_host")
    private boolean isHost;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Group getGroups() {
		return groups;
	}

	public void setGroups(Group groups) {
		this.groups = groups;
	}

	public boolean isHost() {
		return isHost;
	}

	public void setHost(boolean isHost) {
		this.isHost = isHost;
	}

}
