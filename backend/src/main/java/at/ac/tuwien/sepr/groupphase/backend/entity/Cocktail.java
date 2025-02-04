package at.ac.tuwien.sepr.groupphase.backend.entity;

import java.util.List;
import java.util.Set;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CocktailDetailDto;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;

/**
 * Entity of table Cocktail.
 */
@Entity
public class Cocktail implements Comparable<Cocktail> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "image_path")
    private String imagePath;

    @Column(name = "instructions", columnDefinition = "TEXT")
    private String instructions;

    @OneToMany(mappedBy = "cocktail")
    private List<CocktailIngredients> cocktailIngredients;

    @OneToMany(mappedBy = "cocktail", cascade = CascadeType.REMOVE)
    private Set<Feedback> feedbacks;

    @ManyToMany
    private Set<Preference> preferences;

    @ManyToMany
    private Set<ApplicationGroup> applicationGroups;

    public Cocktail() {
    }

    public Cocktail(Long id, String name, String imagePath) {
        this.id = id;
        this.name = name;
        this.imagePath = imagePath;
    }

    public Cocktail(CocktailDetailDto toCreate) {
        this.name = toCreate.getName();
        this.imagePath = toCreate.getImagePath();
        this.instructions = toCreate.getInstructions();
    }

    public Set<ApplicationGroup> getApplicationGroups() {
        return applicationGroups;
    }

    public void setApplicationGroups(Set<ApplicationGroup> groups) {
        this.applicationGroups = groups;
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

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public Set<Feedback> getFeedbacks() {
        return feedbacks;
    }

    public void setFeedbacks(Set<Feedback> feedbacks) {
        this.feedbacks = feedbacks;
    }

    public List<CocktailIngredients> getCocktailIngredients() {
        return cocktailIngredients;
    }

    public void setCocktailIngredients(List<CocktailIngredients> cocktailIngredients) {
        this.cocktailIngredients = cocktailIngredients;
    }

    public Set<Preference> getPreferences() {
        return preferences;
    }

    public void setPreferences(Set<Preference> preferences) {
        this.preferences = preferences;
    }

    @Override
    public int compareTo(Cocktail o) {
        return this.getName().compareTo(o.getName());
    }

    /**
     * Group builder.
     */
    public static final class CocktailBuilder {
        private Long id;
        private String name;
        private String imagePath;
        private String instructions;
        private List<CocktailIngredients> cocktailIngredients;
        private Set<Preference> preferences;
        private CocktailBuilder() {
        }

        public static Cocktail.CocktailBuilder cocktail() {
            return new Cocktail.CocktailBuilder();
        }

        public Cocktail.CocktailBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public Cocktail.CocktailBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public Cocktail.CocktailBuilder withImagePath(String imagePath){
            this.imagePath = imagePath;
            return this;
        }

        public Cocktail.CocktailBuilder withInstructions(String instructions){
            this.instructions = instructions;
            return this;
        }

        public Cocktail.CocktailBuilder withCocktailIngredients(List<CocktailIngredients> cocktailIngredients){
            this.cocktailIngredients = cocktailIngredients;
            return this;
        }

        public Cocktail.CocktailBuilder withPreferences(Set<Preference> preferences){
            this.preferences = preferences;
            return this;
        }


        /**
         * Build Cocktail.
         *
         * @return Cocktail
         */
        public Cocktail build() {
            Cocktail cocktail = new Cocktail();
            cocktail.setId(id);
            cocktail.setName(name);
            cocktail.setImagePath(imagePath);
            cocktail.setInstructions(instructions);
            cocktail.setCocktailIngredients(cocktailIngredients);
            cocktail.setPreferences(preferences);
            return cocktail;
        }
    }
}
