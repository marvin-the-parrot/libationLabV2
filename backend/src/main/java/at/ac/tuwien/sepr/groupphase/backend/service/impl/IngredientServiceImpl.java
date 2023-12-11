package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import at.ac.tuwien.sepr.groupphase.backend.api.IngredientApiResponse;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.IngredientGroupDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.IngredientListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.IngredientMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationGroup;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredient;
import at.ac.tuwien.sepr.groupphase.backend.entity.UserGroup;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.IngredientsRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserGroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.IngredientService;
import jakarta.transaction.Transactional;

/**
 * Ingredients service implementation.
 */
@Service
public class IngredientServiceImpl implements IngredientService {

    private final IngredientsRepository ingredientsRepository;
    private final UserGroupRepository userGroupRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final IngredientMapper ingredientMapper;
    private final UserMapper userMapper;
    public static final String SEARCH_INGREDIENT_URL = "https://www.thecocktaildb.com/api/json/v1/1/search.php";
    private final RestTemplate restTemplate;

    @Autowired
    public IngredientServiceImpl(IngredientsRepository ingredientsRepository, UserGroupRepository userGroupRepository,
                                 UserRepository userRepository, GroupRepository groupRepository,
                                 IngredientMapper ingredientMapper, UserMapper userMapper, RestTemplate restTemplate) {
        this.ingredientsRepository = ingredientsRepository;
        this.userGroupRepository = userGroupRepository;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.ingredientMapper = ingredientMapper;
        this.userMapper = userMapper;
        this.restTemplate = restTemplate;
    }

    @Override
    public List<IngredientListDto> searchIngredients(String ingredientsName) throws JsonProcessingException {
        List<Ingredient> searchIngredientsFromDb = ingredientsRepository.findByNameContainingIgnoreCase(ingredientsName);
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
        List<Ingredient> ingredients = ingredientsRepository.findAllByApplicationUserIn(applicationUser);

        for (Ingredient ingredient : ingredients) {
            List<ApplicationUser> ingredientUsers = new ArrayList<>();
            for (UserGroup userGroup : userGroups) {
                for (ApplicationUser user : ingredient.getApplicationUser()) {
                    if (user.equals(userGroup.getUser())) {
                        ingredientUsers.add(userRepository.findByIngredientsAndUserGroups(ingredient, userGroup));
                    }
                }
            }
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
        return ingredientMapper.ingredientToIngredientListDto(ingredientsRepository.findFirst5ByNameNotInAndNameIgnoreCaseContaining(names, searchParams));
    }

    @Override
    public List<IngredientListDto> getUserIngredients() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        ApplicationUser user = userRepository.findByEmail(email);
        if (user == null) {
            throw new NotFoundException("User not found");
        }
        List<Ingredient> userIngredients = ingredientsRepository.findAllByApplicationUser(user);
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
            Ingredient ingredient = ingredientsRepository.findById(ingredientDto.getId())
                .orElseThrow(() -> new NotFoundException("Ingredient not found"));

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
}
