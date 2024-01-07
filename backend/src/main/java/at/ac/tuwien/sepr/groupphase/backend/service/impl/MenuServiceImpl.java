package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CocktailListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MenuRecommendationDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.RecommendedMenuesDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Preference;
import at.ac.tuwien.sepr.groupphase.backend.entity.UserGroup;
import at.ac.tuwien.sepr.groupphase.backend.repository.CocktailRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PreferenceRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserGroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.CocktailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CocktailOverviewDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MenuCocktailsDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.CocktailIngredientMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationGroup;
import at.ac.tuwien.sepr.groupphase.backend.entity.Cocktail;
import at.ac.tuwien.sepr.groupphase.backend.entity.Menu;
import at.ac.tuwien.sepr.groupphase.backend.entity.MenuKey;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.MenuRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.MenuService;

@Service
public class MenuServiceImpl implements MenuService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private PreferenceRepository preferenceRepository;

    @Autowired
    private CocktailRepository cocktailRepository;

    @Autowired
    private UserGroupRepository userGroupRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CocktailIngredientMapper cocktailIngredientMapper;

    @Autowired
    private CocktailService cocktailService;

    @Override
    public MenuCocktailsDto findMenuOfGroup(Long groupId) throws NotFoundException {
        LOGGER.debug("Finding menu of group by id {}", groupId);
        List<Menu> menu = menuRepository.findByGroupId(groupId);
        List<Cocktail> cocktailsMenu = menu.stream()
            .map(Menu::getCocktail).toList();
        List<CocktailOverviewDto> cocktailOverviewDtoList = cocktailIngredientMapper.cocktailToCocktailOverviewDtoList(cocktailsMenu);
        MenuCocktailsDto menuCocktailsDto = new MenuCocktailsDto(groupId, cocktailOverviewDtoList);
        return menuCocktailsDto;
    }

    @Override
    public MenuCocktailsDto create(MenuCocktailsDto toCreate) throws ConflictException {
        LOGGER.debug("Create menu {}", toCreate);
        Long groupId = toCreate.getGroupId();
        menuRepository.deleteByGroupId(groupId);

        ApplicationGroup applicationGroup = groupRepository.findById(groupId).get();
        List<Cocktail> menuCoctails = cocktailIngredientMapper.cocktailOverviewDtoToCocktailList(toCreate.getCocktailsList());

        for (Cocktail eachCocktail : menuCoctails) {
            Menu newMenu = new Menu();
            MenuKey newMenuKey = new MenuKey(eachCocktail.getId(), groupId);
            newMenu.setMenuKey(newMenuKey);
            newMenu.setCocktail(eachCocktail);
            newMenu.setGroup(applicationGroup);
            menuRepository.save(newMenu);
        }

        return toCreate;
    }

    @Override
    public RecommendedMenuesDto createRecommendation(Long groupId, Integer size, Integer numberOfMenues) {
        LOGGER.info("Create recommendation for group {}", groupId);

        // fetch preferences of group from db
        Set<UserGroup> group = userGroupRepository.findAllByIdGroup(groupId);
        if (group == null) {
            throw new NotFoundException("Group with id " + groupId + " not found");
        }

        List<ApplicationUser> users = userRepository.findByUserGroupsIn(group);

        List<Preference> preferences = preferenceRepository.findAllByApplicationUserIsIn(users);

        LinkedHashMap<String, Integer> orderedPreferenceMap = orderedPreferences(preferences);

        List<CocktailOverviewDto> mixableCocktails = cocktailService.getMixableCocktails(groupId);

        // fetch cocktials from db that fulfill atleast one of the listed preferences
        List<Cocktail> cocktails = cocktailRepository.findByPreferencesInAndIdIn(preferences, mixableCocktails.stream().map(CocktailOverviewDto::getId).collect(
            Collectors.toList()));


        // select preference with highest value and select a random cocktail from the list of cocktails that fulfill this preference
        LinkedHashMap<String, Integer> nonFulfilledPreferences = orderedPreferenceMap;

        if (orderedPreferenceMap.size() == 0) {
            throw new IllegalArgumentException("No preferences found for group with id " + groupId);
        }

        List<MenuRecommendationDto> recommendations = new ArrayList<>();
        for (int i = 0; i < numberOfMenues; i++) {
            Collections.shuffle(cocktails, new Random());
            List<Cocktail> shufflesCocktails = new ArrayList<>(cocktails);
            recommendations.add(pickCocktailMenu(size, shufflesCocktails, nonFulfilledPreferences));
        }


        return new RecommendedMenuesDto(groupId, recommendations);

        // calculate LibationValue for the List of cocktails (how many of the preferences are fulfilled by the whole menu)


    }

    private MenuRecommendationDto pickCocktailMenu(Integer size, List<Cocktail> cocktails, LinkedHashMap<String, Integer> nonFulfilledPreferences) {
        List<Cocktail> selectedCocktails = new ArrayList<>();
        Set<String> fulfilledPreferences = new HashSet<>();
        int countFullfilledPreferences = 0;

        while (selectedCocktails.size() < size) {
            countFullfilledPreferences += fulfilledPreferences.size();
            fulfilledPreferences.clear();
            // save size so we can check if we are making progress
            int countPrevLoop = selectedCocktails.size();

            for (String stringPreference : nonFulfilledPreferences.keySet()) {
                if (fulfilledPreferences.contains(stringPreference)) {
                    continue;
                }
                if (selectedCocktails.size() == size) {
                    break;
                }
                for (Cocktail cocktail : cocktails) {
                    if (cocktail.getPreferences().stream().anyMatch(preference -> preference.getName().equals(stringPreference))) {
                        for (Preference preference : cocktail.getPreferences()) {
                            if (nonFulfilledPreferences.containsKey(preference.getName())) {
                                fulfilledPreferences.add(preference.getName());
                            }
                        }
                        selectedCocktails.add(cocktail);
                        cocktails.remove(cocktail);
                        break;

                    }
                }
            }
            // if no progress is made to complete the list of cocktails, abort
            if (countPrevLoop == selectedCocktails.size()) {
                throw new NotFoundException(
                    "Not enough cocktails found for the given preferences to fulfill the menu size. Try decreasing menu size or adding ingredients to the bar.");
            }
        }

        //libation value
        float lv;
        if (countFullfilledPreferences == 0 && fulfilledPreferences.size() == 0) {
            lv = 0;
        } else {
            lv = Math.max((float) countFullfilledPreferences, fulfilledPreferences.size()) / (float) nonFulfilledPreferences.size();
        }
        List<CocktailListDto> cocktailList = cocktailIngredientMapper.cocktailIngredientToCocktailListDto(selectedCocktails);
        return new MenuRecommendationDto(cocktailList, Math.min(lv,1L));

    }

    // creates a sorted list of the groups preferences from most to least occuring
    private LinkedHashMap<String, Integer> orderedPreferences(List<Preference> preferences) {
        // create a sorted list with the groups preferences
        TreeMap<String, Integer> preferenceMap = new TreeMap<>();

        for (Preference preference : preferences) {
            preferenceMap.put(preference.getName(), preference.getApplicationUser().size());
        }

        SortedSet<Map.Entry<String, Integer>> sortedSet = new TreeSet<>(new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                if (o1.getValue().equals(o2.getValue())) {
                    return o1.getKey().compareTo(o2.getKey());
                } else {
                    return o2.getValue().compareTo(o1.getValue());
                }
            }
        });
        sortedSet.addAll(preferenceMap.entrySet());

        LinkedHashMap<String, Integer> orderedPreferenceMap = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : sortedSet) {
            orderedPreferenceMap.put(entry.getKey(), entry.getValue());
        }

        return orderedPreferenceMap;
    }


}
