package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.api.IngredientApiResponse;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CocktailOverviewDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.IngredientGroupDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.IngredientListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.IngredientSuggestionDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.CocktailIngredientMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.IngredientMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationGroup;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Cocktail;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredient;
import at.ac.tuwien.sepr.groupphase.backend.entity.UserGroup;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.CocktailRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.IngredientsRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserGroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.IngredientService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Ingredients service implementation.
 */
@Service
public class IngredientServiceImpl implements IngredientService {

    public static final String SEARCH_INGREDIENT_URL = "https://www.thecocktaildb.com/api/json/v1/1/search.php";
    private final IngredientsRepository ingredientsRepository;
    private final UserGroupRepository userGroupRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final IngredientMapper ingredientMapper;
    private final UserMapper userMapper;
    private final RestTemplate restTemplate;
    private final CocktailRepository cocktailRepository;
    private final CocktailIngredientMapper cocktailIngredientMapper;

    @Autowired
    public IngredientServiceImpl(IngredientsRepository ingredientsRepository, UserGroupRepository userGroupRepository, UserRepository userRepository,
                                 GroupRepository groupRepository, IngredientMapper ingredientMapper, UserMapper userMapper, RestTemplate restTemplate,
                                 CocktailRepository cocktailRepository, CocktailIngredientMapper cocktailIngredientMapper) {
        this.ingredientsRepository = ingredientsRepository;
        this.userGroupRepository = userGroupRepository;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.ingredientMapper = ingredientMapper;
        this.userMapper = userMapper;
        this.restTemplate = restTemplate;
        this.cocktailRepository = cocktailRepository;
        this.cocktailIngredientMapper = cocktailIngredientMapper;
    }

    @Override
    public List<IngredientListDto> searchIngredients(String ingredientsName) throws JsonProcessingException {
        if (ingredientsName.equals("null")) {
            return ingredientMapper.ingredientToIngredientListDto(ingredientsRepository.findAllByOrderByName());
        }
        List<Ingredient> searchIngredientsFromDb = ingredientsRepository.findByNameContainingIgnoreCaseOrderByName(ingredientsName);
        if (!searchIngredientsFromDb.isEmpty()) {
            return ingredientMapper.ingredientToIngredientListDto(searchIngredientsFromDb);
        }
        String url = SEARCH_INGREDIENT_URL + "?i=" + ingredientsName;
        String apiJsonCall = restTemplate.getForObject(url, String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        IngredientApiResponse ingredientResponse = objectMapper.readValue(apiJsonCall, IngredientApiResponse.class);
        return ingredientMapper.ingredientApiToIngredientListDto(ingredientResponse.getIngredients());
    }

    @Transactional
    @Override
    public List<IngredientGroupDto> getAllGroupIngredients(Long groupId) throws NotFoundException {
        ApplicationGroup group = groupRepository.findById(groupId).orElseThrow(() -> new NotFoundException("Group not found"));
        List<ApplicationUser> applicationUser = userGroupRepository.findUsersByGroupId(groupId);

        if (applicationUser == null || applicationUser.isEmpty()) {
            throw new NotFoundException("No users found for group");
        }

        List<UserGroup> userGroups = userGroupRepository.findAllByApplicationGroup(group);

        List<IngredientGroupDto> ingredientGroupDtos = new ArrayList<>();
        List<Ingredient> ingredients = ingredientsRepository.findAllByApplicationUserInOrderByName(applicationUser);

        for (Ingredient ingredient : ingredients) {
            Set<Ingredient> ingredientsToAdd = new HashSet<>();
            Set<UserGroup> userGroupsToAdd = new HashSet<>();
            for (UserGroup userGroup : userGroups) {
                for (ApplicationUser user : ingredient.getApplicationUser()) {
                    if (user.equals(userGroup.getUser())) {
                        ingredientsToAdd.add(ingredient);
                        userGroupsToAdd.add(userGroup);
                    }
                }
            }
            List<ApplicationUser> ingredientUsers = new ArrayList<>(userRepository.findByIngredientsInAndUserGroupsIn(ingredientsToAdd, userGroupsToAdd));
            List<UserListDto> users = userMapper.userToUserListDto(ingredientUsers);
            UserListDto[] usersArray = new UserListDto[users.size()];
            users.toArray(usersArray);

            ingredientGroupDtos.add(ingredientMapper.from(ingredient, usersArray));
        }
        return ingredientGroupDtos;
    }

    @Transactional
    @Override
    public List<IngredientListDto> searchUserIngredients(String searchParams) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        ApplicationUser user = userRepository.findByEmail(email);
        if (user == null) {
            throw new NotFoundException("User not found");
        }
        List<Ingredient> userIngredients = ingredientsRepository.findAllByApplicationUser(user);
        List<String> names = new ArrayList<>();
        for (Ingredient ingredient : userIngredients) {
            names.add(ingredient.getName());
        }
        if (names.isEmpty()) {
            return ingredientMapper.ingredientToIngredientListDto(ingredientsRepository.findFirst10ByNameIgnoreCaseContainingOrderByName(searchParams));
        } else {
            return ingredientMapper.ingredientToIngredientListDto(ingredientsRepository.findFirst10ByNameNotInAndNameIgnoreCaseContainingOrderByName(names, searchParams));
        }
    }

    @Override
    public List<IngredientListDto> getUserIngredients() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        List<ApplicationUser> user = new ArrayList<>();
        ApplicationUser userToAdd = userRepository.findByEmail(email);
        if (userToAdd == null) {
            throw new NotFoundException("User not found");
        }
        user.add(userToAdd);
        List<Ingredient> userIngredients = ingredientsRepository.findAllByApplicationUserInOrderByName(user);
        return ingredientMapper.ingredientToIngredientListDto(userIngredients);
    }

    @Override
    public List<IngredientListDto> addIngredientsToUser(IngredientListDto[] ingredientListDto) throws ConflictException {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        ApplicationUser user = userRepository.findByEmail(email);
        if (user == null) {
            throw new NotFoundException("User not found");
        }

        // Create a new set to store the updated ingredients
        Set<Ingredient> updatedIngredients = new HashSet<>();

        // Iterate through the received ingredient IDs
        for (IngredientListDto ingredientDto : ingredientListDto) {
            // Get the ingredient from the repository using its ID
            Ingredient ingredient = ingredientsRepository.findById(ingredientDto.getId()).orElseThrow(() -> new NotFoundException("Ingredient not found"));

            if (!Objects.equals(ingredientDto.getName(), ingredient.getName())) {
                List<String> conflictException = new ArrayList<>();
                conflictException.add(ingredientDto.getName() + " is not the same as " + ingredient.getName());
                throw new ConflictException("ConflictException", conflictException);
            }
            updatedIngredients.add(ingredient);
        }
        // Update user's ingredients by adding new ingredients and removing missing ones
        user.setIngredients(updatedIngredients);

        // Save the updated ApplicationUser entity
        userRepository.save(user);

        return ingredientMapper.ingredientToIngredientListDto(ingredientsRepository.findAllByApplicationUser(user));

    }

    @Override
    @Transactional
    public List<IngredientSuggestionDto> getIngredientSuggestions(Long groupId) throws NotFoundException, ConflictException {
        // validate request
        ApplicationGroup group = groupRepository.findById(groupId).orElseThrow(() -> new NotFoundException("Group not found"));
        ApplicationUser user = userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        if (user == null) {
            throw new NotFoundException("User not found");
        }
        UserGroup userGroup = userGroupRepository.findByApplicationUserAndApplicationGroup(user, group);
        if (userGroup == null) {
            throw new ConflictException("Getting ingredient suggestions failed.", List.of("User is not a member of the group"));
        }
        if (!userGroup.isHost()) {
            throw new ConflictException("Getting ingredient suggestions failed.", List.of("User is not the host of the group"));
        }


        // get all ingredients from group
        List<IngredientGroupDto> existingIngredients = getAllGroupIngredients(groupId);

        // get all cocktails from the db
        List<Cocktail> allCocktails = cocktailRepository.findAllByOrderByNameAsc();

        // ingredients and list of cocktails that can be mixed with it
        var foundIngredients = new HashMap<Ingredient, List<CocktailOverviewDto>>();

        // iterate over all cocktails, and check if they can be mixed with only one more ingredient
        for (var cocktail : allCocktails) {
            var cocktailIngredients = cocktail.getCocktailIngredients();

            var newIngredients = new ArrayList<Ingredient>();
            for (var cocktailIngredient : cocktailIngredients) {

                if (!containsIngredient(existingIngredients, cocktailIngredient.getIngredient())) {
                    newIngredients.add(cocktailIngredient.getIngredient());
                }
            }

            if (newIngredients.size() != 1) { // skip cocktails that can only be mixed with more than one ingredient (or can already be mixed)
                continue;
            }
            // update found ingredients:
            if (foundIngredients.containsKey(newIngredients.get(0))) {
                var mixableCocktails = new ArrayList<>(foundIngredients.get(newIngredients.get(0)));
                mixableCocktails.add(cocktailIngredientMapper.cocktailToCocktailOverviewDto(cocktail));
                foundIngredients.put(newIngredients.get(0), mixableCocktails);
            } else {
                foundIngredients.put(newIngredients.get(0), List.of(cocktailIngredientMapper.cocktailToCocktailOverviewDto(cocktail)));
            }
        }

        // analyze results
        List<IngredientSuggestionDto> suggestions = new ArrayList<>(foundIngredients.size());
        for (var entry : foundIngredients.entrySet()) {
            suggestions.add(new IngredientSuggestionDto(entry.getKey().getId(), entry.getKey().getName(), entry.getValue()));
        }

        suggestions.sort((s1, s2) -> s2.getPossibleCocktails().size() - s1.getPossibleCocktails().size());

        /* todo: uncomment this if you want to limit the number of suggestions
        if (suggestions.size() > 5) {
            return suggestions.subList(0, 5);
        }
        */
        return suggestions;
    }

    /**
     * Checks if a list of ingredients contains a specific ingredient (by name).
     *
     * @param ingredients list of ingredients
     * @param ingredient ingredient to search for
     * @return true if the ingredient is in the list, false otherwise
     */
    private boolean containsIngredient(List<IngredientGroupDto> ingredients, Ingredient ingredient) {
        for (var i : ingredients) {
            if (i.getName().equalsIgnoreCase(ingredient.getName())) { // todo: there are some ingredients with different capitalization
                return true;
            }
        }
        return false;
    }
}
