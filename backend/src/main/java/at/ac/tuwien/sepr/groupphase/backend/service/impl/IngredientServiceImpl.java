package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import java.util.ArrayList;
import java.util.List;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.IngredientGroupDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.IngredientMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserGroupRepository;
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
    private final IngredientMapper ingredientMapper;
    private final UserMapper userMapper;

    @Autowired
    public IngredientServiceImpl(IngredientsRepository ingredientsRepository, UserGroupRepository userGroupRepository, IngredientMapper ingredientMapper, UserMapper userMapper) {
        this.ingredientsRepository = ingredientsRepository;
        this.userGroupRepository = userGroupRepository;
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
        List<ApplicationUser> applicationUser = userGroupRepository.findUsersByGroupId(groupId);

        if (applicationUser == null || applicationUser.isEmpty()) {
            throw new NotFoundException("No users found for group");
        }

        List<IngredientGroupDto> ingredientGroupDtos = new ArrayList<>();
        List<Ingredient> ingredients = ingredientsRepository.findAllByApplicationUserIn(applicationUser);
        List<UserListDto> users = userMapper.userToUserListDto(applicationUser);
        UserListDto[] useresArray = new UserListDto[users.size()];
        users.toArray(useresArray);
        for (Ingredient ingredient : ingredients) {
            ingredientGroupDtos.add(ingredientMapper.from(ingredient, useresArray));
        }
        return ingredientGroupDtos;
    }

}
