package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CocktailDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CocktailListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CocktailListMenuDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CocktailOverviewDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.FeedbackState;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.IngredientGroupDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MenuCocktailsDetailViewDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MenuCocktailsDetailViewHostDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MenuCocktailsDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MenuRecommendationDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.RecommendedMenuesDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.CocktailIngredientMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.MenuMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationGroup;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Cocktail;
import at.ac.tuwien.sepr.groupphase.backend.entity.CocktailIngredients;
import at.ac.tuwien.sepr.groupphase.backend.entity.Feedback;
import at.ac.tuwien.sepr.groupphase.backend.entity.Preference;
import at.ac.tuwien.sepr.groupphase.backend.entity.UserGroup;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.CocktailRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.FeedbackRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PreferenceRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserGroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.CocktailService;
import at.ac.tuwien.sepr.groupphase.backend.service.IngredientService;
import at.ac.tuwien.sepr.groupphase.backend.service.MenuService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

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

@Service
public class MenuServiceImpl implements MenuService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final GroupRepository groupRepository;
    private final PreferenceRepository preferenceRepository;
    private final CocktailRepository cocktailRepository;
    private final UserGroupRepository userGroupRepository;
    private final UserRepository userRepository;
    private final FeedbackRepository feedbackRepository;
    private final CocktailIngredientMapper cocktailIngredientMapper;
    private final CocktailService cocktailService;
    private final IngredientService ingredientService;
    private final MenuMapper menuMapper;

    @Autowired
    public MenuServiceImpl(GroupRepository groupRepository, PreferenceRepository preferenceRepository,
                           CocktailRepository cocktailRepository, UserGroupRepository userGroupRepository, UserRepository userRepository,
                           FeedbackRepository feedbackRepository, CocktailIngredientMapper cocktailIngredientMapper, CocktailService cocktailService,
                           IngredientService ingredientService, MenuMapper menuMapper) {
        this.groupRepository = groupRepository;
        this.preferenceRepository = preferenceRepository;
        this.cocktailRepository = cocktailRepository;
        this.userGroupRepository = userGroupRepository;
        this.userRepository = userRepository;
        this.feedbackRepository = feedbackRepository;
        this.cocktailIngredientMapper = cocktailIngredientMapper;
        this.cocktailService = cocktailService;
        this.ingredientService = ingredientService;
        this.menuMapper = menuMapper;
    }

    @Transactional
    @Override
    public MenuCocktailsDto findMenuOfGroup(Long groupId) throws NotFoundException {
        ApplicationGroup applicationGroup = groupRepository.findById(groupId).orElse(null);
        if (applicationGroup == null) {
            throw new NotFoundException("Group with id " + groupId + " not found");
        }
        List<Cocktail> cocktailsMenu = new ArrayList<>(applicationGroup.getCocktails().stream().toList());

        applicationGroup.setCocktails(new HashSet<>(cocktailsMenu));
        groupRepository.save(applicationGroup);

        List<CocktailOverviewDto> cocktailOverviewDtoList = cocktailIngredientMapper.cocktailToCocktailOverviewDtoList(cocktailsMenu);

        return new MenuCocktailsDto(groupId, cocktailOverviewDtoList);
    }

    @Transactional
    @Override
    public MenuCocktailsDto create(MenuCocktailsDto toCreate) throws NotFoundException, ConflictException {
        LOGGER.debug("Create menu {}", toCreate);

        Long groupId = toCreate.getGroupId();
        ApplicationGroup applicationGroup = groupRepository.findById(groupId).orElse(null);

        if (applicationGroup == null) {
            throw new NotFoundException("Group with id " + groupId + " not found");
        }

        List<Long> cocktailIds = toCreate.getCocktailsList().stream().map(CocktailOverviewDto::getId).toList();
        Set<Cocktail> cocktails = cocktailRepository.findByIdIn(cocktailIds);

        if (cocktails == null) {
            throw new NotFoundException("Cocktails with ids " + cocktailIds + " not found");
        }
        if (cocktails.size() != cocktailIds.size()) {
            throw new ConflictException("CONFLICT: Not all cocktails found", List.of("the given cocktails do not match the ones found"));
        }

        applicationGroup.setCocktails(cocktails);
        groupRepository.save(applicationGroup);

        return toCreate;
    }

    @Override
    public RecommendedMenuesDto createRecommendation(Long groupId, Integer size, Integer numberOfRandomMenues) throws IllegalArgumentException {
        LOGGER.info("Create recommendation for group {}", groupId);

        if (size == 0) {
            throw new IllegalArgumentException("Size of Menu must be greater than 0");
        }

        // fetch preferences of group from db
        Set<UserGroup> group = userGroupRepository.findAllByIdGroup(groupId);
        if (group == null) {
            throw new NotFoundException("Group with id " + groupId + " not found");
        }

        List<ApplicationUser> users = userRepository.findByUserGroupsIn(group);
        List<Preference> preferences = preferenceRepository.findAllByApplicationUserIsIn(users);

        // order preferences by how many users have them
        LinkedHashMap<String, Integer> orderedPreferenceMap = orderedPreferences(preferences);

        List<CocktailDetailDto> mixableCocktails = cocktailService.getMixableCocktails(groupId);

        // fetch cocktails from db that fulfill latest one of the listed preferences
        List<Cocktail> cocktails =
            cocktailRepository.findAllByPreferencesInAndIdIn(preferences, mixableCocktails.stream().map(CocktailDetailDto::getId).collect(
                Collectors.toList()));

        // order cocktails by which ones fulfill the most preferences
        List<Cocktail> orderedCocktails = orderCocktails(cocktails, orderedPreferenceMap);

        if (orderedPreferenceMap.size() == 0) {
            throw new IllegalArgumentException("No preferences found for group with id " + groupId);
        }

        // generate an optimal menu
        List<MenuRecommendationDto> recommendations = new ArrayList<>();
        recommendations.add(pickCocktailMenu(size, orderedCocktails, orderedPreferenceMap));

        // generate a more randomized less optimal menu
        for (int i = 0; i < numberOfRandomMenues; i++) {
            Collections.shuffle(cocktails, new Random());
            List<Cocktail> shufflesCocktails = new ArrayList<>(cocktails);
            recommendations.add(pickCocktailMenu(size, shufflesCocktails, orderedPreferenceMap));
        }

        return new RecommendedMenuesDto(groupId, recommendations);

    }

    @Override
    public void updateMixableCocktails() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        ApplicationUser user = userRepository.findByEmail(userEmail);
        ApplicationGroup[] groups = groupRepository.getGroupsOfUser(user.getId());
        if (groups == null) {
            throw new NotFoundException("No Groups found");
        }

        for (ApplicationGroup group : groups) {
            List<Cocktail> cocktailsMenu = new ArrayList<>(group.getCocktails().stream().toList());
            List<IngredientGroupDto> groupIngredients = ingredientService.getAllGroupIngredients(group.getId());

            if (!cocktailsMenu.isEmpty()) {
                List<Cocktail> tempCocktailMenu = List.copyOf(cocktailsMenu);
                for (Cocktail cocktail : tempCocktailMenu) {
                    for (CocktailIngredients cocktailIngredient : cocktail.getCocktailIngredients()) {
                        if (groupIngredients.stream().noneMatch(ingredientGroupDto -> groupIngredients.stream().anyMatch(
                            ingredientListDto -> ingredientListDto.getName().equals(cocktailIngredient.getIngredient().getName())))) {
                            cocktailsMenu.remove(cocktail);
                        }
                    }
                }
            }

            group.setCocktails(new HashSet<>(cocktailsMenu));
            groupRepository.save(group);
        }
    }

    @Override
    public MenuCocktailsDetailViewDto findMenuDetailOfGroup(Long groupId) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        ApplicationUser user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new NotFoundException("User not found");
        }

        ApplicationGroup group = groupRepository.findById(groupId).orElseThrow(() -> new NotFoundException("Group not found"));

        Set<Cocktail> cocktailsMenu = group.getCocktails();
        List<Feedback> feedbacks = feedbackRepository.findByApplicationUserAndApplicationGroupAndCocktailIn(user, group, cocktailsMenu);

        List<CocktailListMenuDto> cocktailListMenuDtoList = new ArrayList<>();
        for (Feedback feedback : feedbacks) {
            CocktailListMenuDto cocktailListMenuDto = new CocktailListMenuDto();
            cocktailListMenuDto.setId(feedback.getCocktail().getId());
            cocktailListMenuDto.setName(feedback.getCocktail().getName());
            cocktailListMenuDto.setRating(feedback.getRating());

            cocktailListMenuDtoList.add(cocktailListMenuDto);
        }

        MenuCocktailsDetailViewDto menuCocktailsDetailViewDto = new MenuCocktailsDetailViewDto();
        menuCocktailsDetailViewDto.setGroupId(groupId);
        menuCocktailsDetailViewDto.setCocktailsList(cocktailListMenuDtoList.toArray(new CocktailListMenuDto[0]));

        return menuCocktailsDetailViewDto;
    }

    @Transactional
    @Override
    public MenuCocktailsDetailViewHostDto getMenuWithRatings(Long groupId) throws NotFoundException {
        LOGGER.debug("Get ratings for group {}", groupId);

        List<Feedback> feedbacks =
            feedbackRepository.findByApplicationGroup(groupRepository.findById(groupId).orElseThrow(() -> new NotFoundException("Group not found")));
        List<Cocktail> cocktails = cocktailRepository.findDistinctByFeedbacksIn(feedbacks);

        if (cocktails.isEmpty()) {
            LinkedHashMap<Cocktail, int[]> cocktailRatingsEmpty = new LinkedHashMap<>();
            return menuMapper.cocktailFeedbackToCocktailFeedbackHostDto(cocktailRatingsEmpty, groupId);
        }

        int[] ratings = new int[2];
        LinkedHashMap<Cocktail, int[]> cocktailRatings = new LinkedHashMap<>();
        cocktailRatings.put(cocktails.get(0), ratings);
        int index = 0;

        feedbacks.sort(Comparator.comparing(o -> o.getCocktail().getId()));
        for (Feedback feedback : feedbacks) {
            if (feedback.getCocktail() == cocktailRatings.keySet().toArray()[index]) {
                if (feedback.getRating() != FeedbackState.NotVoted) {
                    if (feedback.getRating() == FeedbackState.Like) {
                        ratings[0]++;
                    } else if (feedback.getRating() == FeedbackState.Dislike) {
                        ratings[1]++;
                    }
                    cocktailRatings.replace(cocktails.get(index), ratings);
                }
            } else {
                ratings = new int[2];
                if (feedback.getRating() == FeedbackState.Like) {
                    ratings[0]++;
                } else if (feedback.getRating() == FeedbackState.Dislike) {
                    ratings[1]++;
                }
                cocktailRatings.put(feedback.getCocktail(), ratings);

                index++;
            }
        }

        return menuMapper.cocktailFeedbackToCocktailFeedbackHostDto(cocktailRatings, groupId);
    }

    /**
     * Orders cocktails from most occurrences of preferences to least.
     *
     * @param cocktails            list of cocktails that fulfill atleast one of the preferences
     * @param orderedPreferenceMap map of preferences ordered by most wanted to least
     * @return list of cocktails
     */
    private List<Cocktail> orderCocktails(List<Cocktail> cocktails, LinkedHashMap<String, Integer> orderedPreferenceMap) {

        TreeMap<Cocktail, Integer> cocktailPreferenceMap = new TreeMap<>();

        SortedSet<Map.Entry<Cocktail, Integer>> sortedCocktails = new TreeSet<>(new Comparator<Map.Entry<Cocktail, Integer>>() {
            public int compare(Map.Entry<Cocktail, Integer> o1, Map.Entry<Cocktail, Integer> o2) {
                if (o1.getValue().equals(o2.getValue())) {
                    return o1.getKey().compareTo(o2.getKey());
                } else {
                    return o2.getValue().compareTo(o1.getValue());
                }
            }
        });

        for (Cocktail cocktail : cocktails) {
            int satisfiesPreferences = 0;

            for (Preference preference : cocktail.getPreferences()) {
                if (orderedPreferenceMap.containsKey(preference.getName())) {
                    satisfiesPreferences++;
                }
            }
            cocktailPreferenceMap.put(cocktail, satisfiesPreferences);
        }

        sortedCocktails.addAll(cocktailPreferenceMap.entrySet());

        List<Cocktail> orderedCocktails = new ArrayList<>();
        for (Map.Entry<Cocktail, Integer> entry : sortedCocktails) {
            orderedCocktails.add(entry.getKey());
        }

        return orderedCocktails;
    }

    /**
     * creates a cocktail menu based on the groups preferences.
     *
     * @param size                    how many cocktails should be included in the final list
     * @param cocktails               list of cocktails to pick from
     * @param nonFulfilledPreferences list of preferences
     * @return A cocktail Menu
     */
    private MenuRecommendationDto pickCocktailMenu(Integer size, List<Cocktail> cocktails, LinkedHashMap<String, Integer> nonFulfilledPreferences) {
        List<Cocktail> selectedCocktails = new ArrayList<>();
        Set<String> fulfilledPreferences = new HashSet<>();
        int countFullfilledPreferences = 0;

        while (selectedCocktails.size() < size) {
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
                                countFullfilledPreferences++;
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
        return new MenuRecommendationDto(cocktailList, lv);

    }

    /**
     * Orders the preferences by most wanted to least.
     *
     * @param preferences list of preferences
     * @return ordered list of preferences
     */
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
