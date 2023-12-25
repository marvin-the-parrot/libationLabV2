package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * Embedded Id of entity Menu.
 */
@Embeddable
public class MenuKey {

    @Column(name = "cocktail_id")
    public Long cocktail;

    @Column(name = "group_id")
    private Long group;

    public MenuKey(Long cocktail, Long group) {
        this.cocktail = cocktail;
        this.group = group;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MenuKey that = (MenuKey) o;

        if (!cocktail.equals(that.cocktail)) {
            return false;
        }
        return group.equals(that.group);
    }

    @Override
    public int hashCode() {
        int result = cocktail.hashCode();
        result = 31 * result + group.hashCode();
        return result;
    }

    public MenuKey() {

    }
}
