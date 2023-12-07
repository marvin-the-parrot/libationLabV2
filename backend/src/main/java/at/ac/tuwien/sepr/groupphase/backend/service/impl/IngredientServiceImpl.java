package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import java.util.ArrayList;
import java.util.List;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.IngredientGroupDto;
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

}
