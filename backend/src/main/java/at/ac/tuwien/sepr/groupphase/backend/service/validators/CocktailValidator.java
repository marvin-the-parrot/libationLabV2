package at.ac.tuwien.sepr.groupphase.backend.service.validators;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CocktailDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.GroupCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserListDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.UserGroup;
import at.ac.tuwien.sepr.groupphase.backend.entity.UserGroupKey;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.CocktailRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserGroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Validates requests for the group endpoint.
 */
@Component
public class CocktailValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * Validates a group that should be created.
     *
     * @param toCreate the group to create
     * @throws ValidationException if the creation data given for the group
     *                             is in itself incorrect (no name, name too long …)
     */
    public void validateForCreate(CocktailDetailDto toCreate) throws ValidationException {
        LOGGER.trace("validateForCreate({})", toCreate);

        // check for validation errors
        List<String> validationErrors = getValidationErrors(toCreate);

        if (!validationErrors.isEmpty()) {
            throw new ValidationException("Validation of group for create failed", validationErrors);
        }

    }

    /**
     * Validates a group.
     *
     * @param cocktail the group to validate
     * @return a list of validation errors (empty if no errors)
     */
    private List<String> getValidationErrors(CocktailDetailDto cocktail) {
        List<String> validationErrors = new LinkedList<>();

        // name
        if (cocktail.getName() == null || cocktail.getName().isEmpty()) {
            validationErrors.add("Cocktail name must not be empty");
        } else if (cocktail.getName().length() > 255) {
            validationErrors.add("Cocktail name must not be longer than 255 characters");
        }

        if (cocktail.getIngredients().isEmpty()){
            validationErrors.add("Cocktail needs to include at least one ingredient");
        } else if (cocktail.getIngredients().size() > 20){
            validationErrors.add("Cocktail can include at most 20 ingredients");
        }

        if (cocktail.getPreferenceName().isEmpty()){
            validationErrors.add("Cocktail needs to include at least one preference");
        } else if (cocktail.getPreferenceName().size() > 30){
            validationErrors.add("Cocktail can include at most 20 preferences");
        }

        return validationErrors;
    }
}
