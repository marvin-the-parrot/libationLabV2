package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.IngredientListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PreferenceListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.IngredientMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.PreferenceMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredient;
import at.ac.tuwien.sepr.groupphase.backend.entity.Preference;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.IngredientService;
import at.ac.tuwien.sepr.groupphase.backend.service.PreferenceService;
import at.ac.tuwien.sepr.groupphase.backend.repository.PreferenceRepository;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Preference service implementation.
 */
@Service
public class PreferenceServiceImpl implements PreferenceService {
    private final PreferenceRepository preferenceRepository;
    private final UserRepository userRepository;
    private final PreferenceMapper preferenceMapper;


    @Autowired
    public PreferenceServiceImpl(PreferenceRepository preferenceRepository, UserRepository userRepository, PreferenceMapper preferenceMapper) {
        this.preferenceRepository = preferenceRepository;
        this.userRepository = userRepository;
        this.preferenceMapper = preferenceMapper;
    }

    @Transactional
    @Override
    public List<PreferenceListDto> searchUserPreferences(String searchParams) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        ApplicationUser user = userRepository.findByEmail(email);
        if (user == null) {
            throw new NotFoundException("User not found");
        }
        List<Preference> userPreferences = preferenceRepository.findAllByApplicationUser(user);
        List<String> names = new ArrayList<>();
        for (Preference preference : userPreferences) {
            names.add(preference.getName());
        }
        return preferenceMapper.preferenceToPreferenceListDto(preferenceRepository.findFirst5ByNameNotInAndNameIgnoreCaseContaining(names, searchParams));
    }
}
