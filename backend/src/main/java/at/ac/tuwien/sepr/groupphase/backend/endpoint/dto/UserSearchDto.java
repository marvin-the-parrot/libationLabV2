package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public class UserSearchDto {

    private String name;
    private Integer limit;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }
}
