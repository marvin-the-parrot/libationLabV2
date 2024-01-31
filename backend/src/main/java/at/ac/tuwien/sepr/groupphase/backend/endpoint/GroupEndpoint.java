package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CocktailDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.GroupCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.GroupOverviewDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserListGroupDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.GroupMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.UserGroup;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.security.SecurityRolesEnum;
import at.ac.tuwien.sepr.groupphase.backend.service.CocktailService;
import at.ac.tuwien.sepr.groupphase.backend.service.GroupService;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

/**
 * Group endpoint controller.
 */
@RestController
@RequestMapping(path = GroupEndpoint.BASE_PATH)
public class GroupEndpoint {

    static final String BASE_PATH = "/api/v1/groups";
    private static final String ROLE_USER = SecurityRolesEnum.Roles.ROLE_USER;
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final GroupService groupService;
    private final GroupMapper groupMapper;
    private final UserService userService;
    private final CocktailService cocktailService;

    @Autowired
    public GroupEndpoint(GroupService groupService, GroupMapper groupMapper, UserService userService, CocktailService cocktailService) {
        this.groupService = groupService;
        this.groupMapper = groupMapper;
        this.userService = userService;
        this.cocktailService = cocktailService;
    }

    /**
     * Get a list of groups that this viewer is part of.
     *
     * @return Array of GroupOverviewDto
     */
    @Secured(ROLE_USER)
    @GetMapping()
    @Transactional
    @Operation(summary = "Get a list of groups that this viewer is part of", security = @SecurityRequirement(name = "apiKey"))
    public GroupOverviewDto[] findGroupsByUser() {
        LOGGER.info("GET " + BASE_PATH);
        List<UserGroup> userGroupMatchings = groupService.findGroupsByUser();
        List<GroupOverviewDto> groupOverviewDtos = new ArrayList<>();
        for (UserGroup group : userGroupMatchings) {
            GroupOverviewDto groupOverviewDto;
            groupOverviewDto = groupMapper.grouptToGroupOverviewDto(group.getGroup());
            List<UserListGroupDto> users = userService.findUsersByGroup(group.getGroup());
            groupOverviewDto.setMembers(users.toArray(new UserListGroupDto[0]));

            //set host
            for (UserListGroupDto user : users) {
                if (user.isHost()) {
                    groupOverviewDto.setHost(user);
                }
            }
            groupOverviewDtos.add(groupOverviewDto);
        }

        return groupOverviewDtos.toArray(new GroupOverviewDto[0]);
    }

    @Secured(ROLE_USER)
    @GetMapping(value = "/{id}")
    @Transactional
    @Operation(summary = "Get detailed information about a specific group", security = @SecurityRequirement(name = "apiKey"))
    public GroupOverviewDto find(@PathVariable Long id) throws ValidationException {
        LOGGER.info("GET " + BASE_PATH + "/{}", id);

        return groupService.findGroupById(id);
    }

    /**
     * Creating a new group entry.
     *
     * @param toCreate the group entry to create
     * @return the created group entry
     * @throws ValidationException if the data is not valid
     * @throws ConflictException   if the data conflicts with existing data
     */
    @Secured(ROLE_USER)
    @PostMapping()
    @Operation(security = @SecurityRequirement(name = "apiKey"))
    @ResponseStatus(HttpStatus.CREATED)
    public GroupCreateDto create(@RequestBody GroupCreateDto toCreate) throws ValidationException, ConflictException {
        LOGGER.info("POST " + BASE_PATH + "/{}", toCreate);
        LOGGER.debug("Body of request:\n{}", toCreate);
        //TODO no apiKey needed for SecurityContext
        LOGGER.debug("Body of request:\n{}", toCreate);
        return groupService.create(toCreate);
    }

    /**
     * Update an existing group entry.
     *
     * @param id       the id of the group
     * @param toUpdate the group entry to update
     * @return the updated group entry
     * @throws ValidationException if the data is not valid
     * @throws ConflictException   if the data conflicts with existing data
     */
    @Secured(ROLE_USER)
    @PutMapping("{id}")
    @Operation(security = @SecurityRequirement(name = "apiKey"))
    public GroupCreateDto update(@PathVariable long id, @RequestBody GroupCreateDto toUpdate) throws ValidationException, ConflictException {
        LOGGER.info("PUT " + BASE_PATH + "/{}", id);
        LOGGER.debug("Body of request:\n{}", toUpdate);

        toUpdate.setId(id);
        try {
            return groupService.update(toUpdate);
        } catch (NotFoundException e) {
            HttpStatus status = HttpStatus.NOT_FOUND;
            logClientError(status, "Group to update not found", e);
            throw new ResponseStatusException(status, e.getMessage(), e);
        }
    }

    @Secured(ROLE_USER)
    @PutMapping("{groupId}/{userId}")
    @Operation(security = @SecurityRequirement(name = "apiKey"))
    @ResponseStatus(HttpStatus.OK)
    public void makeMemberHost(@PathVariable Long groupId, @PathVariable Long userId) throws ValidationException {
        LOGGER.info("PUT " + BASE_PATH + "/{}/{}", groupId, userId);
        try {
            groupService.makeMemberHost(groupId, userId);
        } catch (NotFoundException e) {
            HttpStatus status = HttpStatus.NOT_FOUND;
            logClientError(status, "Group member to make host not found", e);
            throw new ResponseStatusException(status, e.getMessage(), e);
        }
    }

    /**
     * Delete group.
     *
     * @param id the id of the group
     */
    @Secured(ROLE_USER)
    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(security = @SecurityRequirement(name = "apiKey"))
    public void delete(@PathVariable Long id) throws ValidationException, ConflictException {
        LOGGER.info("DELETE " + BASE_PATH + "/{}", id);
        try {
            groupService.deleteGroup(id);
        } catch (NotFoundException e) {
            HttpStatus status = HttpStatus.NOT_FOUND;
            logClientError(status, "Group to delete not found", e);
            throw new ResponseStatusException(status, e.getMessage(), e);
        }
    }

    /**
     * Removes a member from a group. This action can only be performed by the member itself or the host.
     *
     * @param groupId the id of the group
     * @param userId  the id of member to be deleted
     */
    @Secured(ROLE_USER)
    @DeleteMapping("{groupId}/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(security = @SecurityRequirement(name = "apiKey"))
    public void removeMemberFromGroup(@PathVariable Long groupId, @PathVariable Long userId) throws ValidationException {
        LOGGER.info("DELETE " + BASE_PATH + "/{}/{}", groupId, userId);
        try {
            groupService.deleteMember(groupId, userId);
        } catch (NotFoundException e) {
            HttpStatus status = HttpStatus.NOT_FOUND;
            logClientError(status, "Group member to delete not found", e);
            throw new ResponseStatusException(status, e.getMessage(), e);
        }
    }

    /**
     * Searching for member of group.
     *
     * @param groupId the id of the group
     * @return list of matched user
     */
    @Secured(ROLE_USER)
    @GetMapping("searchGroupMember/{groupId}")
    @ResponseStatus(HttpStatus.OK)
    public List<UserListDto> searchGroupMember(@PathVariable Long groupId) {
        LOGGER.info("GET " + BASE_PATH + "/searchGroupMember/{}", groupId);
        return groupService.searchForMember(groupId);
    }

    @Secured(ROLE_USER)
    @GetMapping("{groupId}/mixables")
    @ResponseStatus(HttpStatus.OK)
    public List<CocktailDetailDto> getMixableCocktails(@PathVariable Long groupId) throws JsonProcessingException {
        LOGGER.info("GET " + BASE_PATH + "/" + groupId + "/mixables");
        return cocktailService.getMixableCocktails(groupId);
    }

    private void logClientError(HttpStatus status, String message, Exception e) {
        LOGGER.warn("{} {}: {}: {}", status.value(), message, e.getClass().getSimpleName(), e.getMessage());
    }

}
