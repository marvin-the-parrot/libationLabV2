package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

/**
 * Dto to send group detail data to the frontend.
 */
public class GroupOverviewDto {

    private Long id;
    private String name;
    private boolean isHost;
    private String cocktail;
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

    /**
     * Group detail dto builder.
     */
    public static final class GroupDetailDtoBuilder {
        private Long id;
        private String name;
        private boolean isHost;
        private String cocktail;
        private Long membersId;

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
            this.cocktail = cocktail;
            return this;
        }

        public GroupDetailDtoBuilder withMembersId(Long membersId) {
            this.membersId = membersId;
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
            groupOverviewDto.setHost(isHost);
            groupOverviewDto.setCocktail(cocktail);
            groupOverviewDto.setMembersId(membersId);
            return groupOverviewDto;
        }
    }
}
