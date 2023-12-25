package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import java.lang.invoke.MethodHandles;
import java.util.List;

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
    private CocktailIngredientMapper cocktailIngredientMapper;

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
}
