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
    private ApplicationUser applicationUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("group_id")
    @JoinColumn(name = "group_id", nullable = false)
    private ApplicationGroup applicationGroup;

    @Column(name = "is_host")
    private boolean isHost;

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

}
