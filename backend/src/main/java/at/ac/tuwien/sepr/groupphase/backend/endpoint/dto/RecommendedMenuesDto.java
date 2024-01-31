package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.util.List;

/**
 * DTO for a recommended menu.
 */
public class RecommendedMenuesDto {

    Long id;
    List<MenuRecommendationDto> menuList;

    public RecommendedMenuesDto(Long id, List<MenuRecommendationDto> cocktailsList) {
        this.id = id;
        this.menuList = cocktailsList;
    }

    public RecommendedMenuesDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<MenuRecommendationDto> getMenuList() {
        return menuList;
    }

    public void setMenuList(List<MenuRecommendationDto> menuList) {
        this.menuList = menuList;
    }
}
