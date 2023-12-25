package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

/**
 * Entity of table Menu.
 */
@Entity
@Table(name = "menu")
public class Menu {

    @EmbeddedId
    private MenuKey menuKey;

    @ManyToOne
    @MapsId("cocktailId")
    @JoinColumn(name = "cocktail_id")
    private Cocktail cocktail;

    @ManyToOne
    @MapsId("groupId")
    @JoinColumn(name = "group_id")
    private ApplicationGroup group;

    public MenuKey getMenuKey() {
        return menuKey;
    }

    public void setMenuKey(MenuKey menuKey) {
        this.menuKey = menuKey;
    }

    public Cocktail getCocktail() {
        return cocktail;
    }

    public void setCocktail(Cocktail cocktail) {
        this.cocktail = cocktail;
    }

    public ApplicationGroup getGroup() {
        return group;
    }

    public void setGroup(ApplicationGroup group) {
        this.group = group;
    }
}
