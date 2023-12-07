package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.IngredientGroupDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.IngredientListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.IngredientSearchExistingUserDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.IngredientMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationGroup;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.UserGroup;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserGroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredient;
import at.ac.tuwien.sepr.groupphase.backend.repository.IngredientsRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.IngredientService;

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

    @Autowired
    public IngredientServiceImpl(IngredientsRepository ingredientsRepository, UserGroupRepository userGroupRepository,
                                 UserRepository userRepository, GroupRepository groupRepository,
                                 IngredientMapper ingredientMapper, UserMapper userMapper) {
        this.ingredientsRepository = ingredientsRepository;
        this.userGroupRepository = userGroupRepository;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.ingredientMapper = ingredientMapper;
        this.userMapper = userMapper;
    }

    @Override
    public List<Ingredient> searchIngredients(String ingredientsName) {
        return ingredientsRepository.searchIngredients(ingredientsName);
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
    public List<IngredientListDto> searchUserIngredients(IngredientSearchExistingUserDto searchParams) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        ApplicationUser user = userRepository.findByEmail(email);
        List<ApplicationUser> userList = new ArrayList<>();
        userList.add(user);
        List<Ingredient> userIngredients = ingredientsRepository.findAllByApplicationUser(user);
        List<String> names = new ArrayList<>();
        for (Ingredient ingredient : userIngredients) {
            names.add(ingredient.getName());
        }
        return ingredientMapper.ingredientToIngredientListDto(ingredientsRepository.findFirst5ByNameNotInAndNameIgnoreCaseContaining(names, searchParams.getName()));
    }

    @Override
    public List<IngredientListDto> getUserIngredients() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        ApplicationUser user = userRepository.findByEmail(email);
        List<Ingredient> userIngredients = ingredientsRepository.findAllByApplicationUser(user);
        return ingredientMapper.ingredientToIngredientListDto(userIngredients);
    }

    @Override
    public List<IngredientListDto> addIngredientsToUser(IngredientListDto[] ingredientListDto) {
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

            updatedIngredients.add(ingredient);
        }
        // Update user's ingredients by adding new ingredients and removing missing ones
        user.setIngredients(updatedIngredients);

        // Save the updated ApplicationUser entity
        userRepository.save(user);

        return ingredientMapper.ingredientToIngredientListDto(ingredientsRepository.findAllByApplicationUser(user));

    }
}
