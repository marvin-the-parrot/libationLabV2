package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;

/**
 * Dto to send group detail data to the frontend.
 */
public class GroupOverviewDto {

    private Long id;
    private String name;
    private String cocktails;
    private UserListGroupDto host;

    public UserListGroupDto getHost() {
        return host;
    }

    public void setHost(UserListGroupDto host) {
        this.host = host;
    }

    private UserListGroupDto[] members;

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

    public String getCocktail() {
        return cocktails;
    }

    public void setCocktail(String cocktail) {
        this.cocktails = cocktail;
    }

    public UserListGroupDto[] getMembers() {
        return members;
    }

    public void setMembers(UserListGroupDto[] members) {
        this.members = members;
    }

    @Override
    public String toString() {
        return "Message{"
            + "id=" + id
            + ", name=" + name
            + ", cocktails='" + cocktails + '\''
            + ", members='" + members + '\''
            + '}';
    }

    /**
     * Group detail dto builder.
     */
    public static final class GroupDetailDtoBuilder {
        private Long id;
        private String name;
        private boolean isHost;
        private String cocktails;
        private UserListGroupDto[] members;

        public GroupDetailDtoBuilder() {
        }

        /**
         * Group detail dto builder.
         *
         * @return GroupDetailDtoBuilder
         */
        public static GroupDetailDtoBuilder groupDetailDto() {
            return new GroupDetailDtoBuilder();
        }

        public GroupDetailDtoBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public GroupDetailDtoBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public GroupDetailDtoBuilder withIsHost(boolean isHost) {
            this.isHost = isHost;
            return this;
        }

        public GroupDetailDtoBuilder withCocktail(String cocktail) {
            this.cocktails = cocktail;
            return this;
        }

        public GroupDetailDtoBuilder withMembers(ApplicationUser[] members) {
            UserListGroupDto[] userListDto = new UserListGroupDto[members.length];
            for (int i = 0; i < members.length; i++) {
                userListDto[i] = new UserListGroupDto(members[i].getId(), members[i].getName(), false);
            }
            return this;
        }

        /**
         * Group detail dto builder.
         *
         * @return GroupDetailDtoBuilder
         */
        public GroupOverviewDto build() {
            GroupOverviewDto groupOverviewDto = new GroupOverviewDto();
            groupOverviewDto.setId(id);
            groupOverviewDto.setName(name);
            groupOverviewDto.setCocktail(cocktails);
            groupOverviewDto.setMembers(members);
            return groupOverviewDto;
        }
    }
}
