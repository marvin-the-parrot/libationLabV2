package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PreferenceListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.PreferenceMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Preference;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.PreferenceService;
import at.ac.tuwien.sepr.groupphase.backend.repository.PreferenceRepository;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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
        if (names.isEmpty()) {
            return preferenceMapper.preferenceToPreferenceListDto(preferenceRepository.findFirst10ByNameIgnoreCaseContainingOrderByName(searchParams));
        } else {
            return preferenceMapper.preferenceToPreferenceListDto(preferenceRepository.findFirst10ByNameNotInAndNameIgnoreCaseContainingOrderByName(names, searchParams));
        }

    }

    @Override
    public List<PreferenceListDto> getUserPreferences() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        List<ApplicationUser> user = new ArrayList<>();
        ApplicationUser userToAdd = userRepository.findByEmail(email);
        if (userToAdd == null) {
            throw new NotFoundException("User not found");
        }
        user.add(userToAdd);

        List<Preference> userPreferences = preferenceRepository.findAllByApplicationUserInOrderByName(user);
        return preferenceMapper.preferenceToPreferenceListDto(userPreferences);
    }

    @Override
    public List<PreferenceListDto> addPreferencesToUser(PreferenceListDto[] preferenceListDto) throws ConflictException {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        ApplicationUser user = userRepository.findByEmail(email);
        if (user == null) {
            throw new NotFoundException("User not found");
        }

        // Create a new set to store the updated preferences
        Set<Preference> updatedPreferences = new HashSet<>();

        // Iterate through the received preference IDs
        for (PreferenceListDto preferenceDto : preferenceListDto) {
            // Get the preference from the repository using its ID
            Preference preference = preferenceRepository.findById(preferenceDto.getId()).orElseThrow(() -> new NotFoundException("Preference not found"));

            if (!Objects.equals(preferenceDto.getName(), preference.getName())) {
                List<String> conflictException = new ArrayList<>();
                conflictException.add(preferenceDto.getName() + " is not the same as " + preference.getName());
                throw new ConflictException("ConflictException", conflictException);
            }
            updatedPreferences.add(preference);
        }
        // Update user's preferences by adding new preferences and removing missing ones
        user.setPreferences(updatedPreferences);

        // Save the updated ApplicationUser entity
        userRepository.save(user);

        return preferenceMapper.preferenceToPreferenceListDto(preferenceRepository.findAllByApplicationUser(user));

    }
}
