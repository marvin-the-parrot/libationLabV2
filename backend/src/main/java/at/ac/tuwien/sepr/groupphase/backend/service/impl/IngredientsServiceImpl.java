package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredients;
import at.ac.tuwien.sepr.groupphase.backend.repository.IngredientsRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.IngredientsService;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * Ingredients service implementation.
 */
@Service
public class IngredientsServiceImpl implements IngredientsService {

  @Autowired
  private IngredientsRepository ingredientsRepository;
    
  @Override
  public Optional<Ingredients> searchIngredients(String ingredientsName) {
    return ingredientsRepository.searchIngredients(ingredientsName);
  }

}
