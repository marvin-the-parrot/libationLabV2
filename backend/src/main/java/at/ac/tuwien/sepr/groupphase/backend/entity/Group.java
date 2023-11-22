package at.ac.tuwien.sepr.groupphase.backend.entity;

import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;

@Entity
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private boolean isHost;

    // TODO: change to cockatil entity
    @Column()
    private String cocktail;

    @ManyToMany
    @JoinTable(
            name = "member_gruppen",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "member_id"))
    private Set<Member> members;
    
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

    public boolean isHost() {
        return isHost;
    }

    public void setHost(boolean host) {
        isHost = host;
    }

    public String getCocktail() {
        return cocktail;
    }

    public void setCocktail(String cocktail) {
        this.cocktail = cocktail;
    }

	public Set<Member> getMembers() {
		return members;
	}

	public void setMembers(Set<Member> members) {
		this.members = members;
	}

    public static final class GroupBuilder {
        private Long id;
        private String name;
        private boolean isHost;
        private String cocktail;
        private Set<Member> members;
        
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

        public GroupBuilder withIsHost(boolean isHost) {
            this.isHost = isHost;
            return this;
        }

        public GroupBuilder withCocktail(String cocktail) {
            this.cocktail = cocktail;
            return this;
        }

        public GroupBuilder withMembers(Set<Member> members) {
            this.members = members;
            return this;
        }

        public Group build() {
            Group group = new Group();
            group.setId(id);
            group.setName(name);
            group.setHost(isHost);
            group.setCocktail(cocktail);
            group.setMembers(members);
            return group;
        }
    }

}
