package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import java.util.List;
import java.util.Set;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationGroup;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.UserGroup;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserGroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.GroupService;
import at.ac.tuwien.sepr.groupphase.backend.service.UserGroupService;
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
    private final GroupService groupService;
    private final UserGroupRepository userGroupRepository;

    @Autowired
    public IngredientServiceImpl(IngredientsRepository ingredientsRepository, GroupService groupService, UserGroupRepository userGroupRepository) {
        this.ingredientsRepository = ingredientsRepository;
        this.groupService = groupService;
        this.userGroupRepository = userGroupRepository;
    }

    @Override
    public List<Ingredient> searchIngredients(String ingredientsName) {
        return ingredientsRepository.searchIngredients(ingredientsName);
    }

    @Override
    public List<Ingredient> getAllGroupIngredients(Long groupId) {
        List<ApplicationUser> applicationUser = userGroupRepository.findUsersByGroupId(groupId);
        return ingredientsRepository.findAllByApplicationUserIn(applicationUser);
    }

}
