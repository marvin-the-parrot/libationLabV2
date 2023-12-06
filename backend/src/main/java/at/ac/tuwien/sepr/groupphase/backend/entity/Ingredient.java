package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToMany;

import java.util.Set;

/**
 * Entity of table Ingredients.
 */
@Entity
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToMany(mappedBy = "ingredients")
    Set<ApplicationUser> applicationUsers;

    public Ingredient() {
    }

    public Ingredient(Long id, String name) {
        this.id = id;
        this.name = name;
    }

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

    public Set<ApplicationUser> getApplicationUsers() {
        return applicationUsers;
    }

    public void setApplicationUsers(Set<ApplicationUser> applicationUsers) {
        this.applicationUsers = applicationUsers;
    }

    public static final class IngredientsBuilder {

        private Long id;
        private String name;
        private Set<ApplicationUser> applicationUsers;

        private IngredientsBuilder() {
        }

        public static IngredientsBuilder ingredients() {
            return new IngredientsBuilder();
        }

        public IngredientsBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public IngredientsBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public IngredientsBuilder withApplicationUsers(Set<ApplicationUser> applicationUsers) {
            this.applicationUsers = applicationUsers;
            return this;
        }

        public Ingredient build() {
            Ingredient ingredient = new Ingredient();
            ingredient.setId(id);
            ingredient.setName(name);
            ingredient.setApplicationUsers(applicationUsers);
            return ingredient;
        }
    }
}
