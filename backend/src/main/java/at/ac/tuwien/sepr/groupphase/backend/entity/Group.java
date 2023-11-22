package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.*;

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

    // TODO: many to many relationship with user
    @Column(nullable = false)
    private Long membersId;

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

    public Long getMembersId() {
        return membersId;
    }

    public void setMembersId(Long membersId) {
        this.membersId = membersId;
    }

    @Override
    public String toString() {
        return "Message{"
            + "id=" + id
            + ", name=" + name
            + ", host='" + isHost + '\''
            + ", cocktails='" + cocktail + '\''
            + ", members='" + membersId + '\''
            + '}';
    }


    public static final class GroupBuilder {
        private Long id;
        private String name;
        private boolean isHost;
        private String cocktail;
        private Long membersId;

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

        public GroupBuilder withMembersId(Long membersId) {
            this.membersId = membersId;
            return this;
        }

        public Group build() {
            Group group = new Group();
            group.setId(id);
            group.setName(name);
            group.setHost(isHost);
            group.setCocktail(cocktail);
            group.setMembersId(membersId);
            return group;
        }
    }
}
