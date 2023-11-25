package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.GroupDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.GroupMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.lang.invoke.MethodHandles;
import java.util.Optional;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;


/**
 * Group endpoint controller.
 */
@RestController
@RequestMapping(path = GroupEndpoint.BASE_PATH)
public class GroupEndpoint {

  static final String BASE_PATH = "/api/v1/groups";

  private static final Logger LOGGER = 
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final GroupService groupService;
  private final GroupMapper groupMapper;

  @Autowired
  public GroupEndpoint(GroupService groupService, GroupMapper groupMapper) {
    this.groupService = groupService;
    this.groupMapper = groupMapper;
  }

  @Secured("ROLE_USER")
  @GetMapping(value = "/{id}")
  @Operation(summary = "Get detailed information about a specific group", 
      security = @SecurityRequirement(name = "apiKey"))
  public GroupDetailDto find(@PathVariable Long id) {
    LOGGER.info("GET /api/v1/groups/{}", id);
    return groupMapper.groupToGroupDetailDto(groupService.findOne(id));
  }

  /**
  * Creating a new group entry.
  *
  * @param toCreate the group entry to create
  * @return the created group entry
  * @throws ValidationException if the data is not valid
  * @throws ConflictException   if the data conflicts with existing data
  */
  @Secured("ROLE_ADMIN")
  @PostMapping()
  @Operation(security = @SecurityRequirement(name = "apiKey"))
  @ResponseStatus(HttpStatus.CREATED)
  public GroupDetailDto create(@RequestBody GroupDetailDto toCreate) 
      throws ValidationException, ConflictException {
    LOGGER.info("POST " + BASE_PATH + "/{}", toCreate);
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
  @Secured("ROLE_ADMIN")
  @PutMapping("{id}")
  public GroupDetailDto update(@PathVariable long id, @RequestBody GroupDetailDto toUpdate) 
      throws ValidationException, ConflictException {
    LOGGER.info("PUT " + BASE_PATH + "/{}", toUpdate);
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


  /**
  * Deleting group entry by id, only possible by host.
  *
  * @param id     the id of the group
  * @param userId the id of the host
  */
  @Secured("ROLE_ADMIN")
  @DeleteMapping("{id}/{userId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(security = @SecurityRequirement(name = "apiKey"))
  public void delete(@PathVariable Long id, @PathVariable Long userId) 
      throws ValidationException, ConflictException {
    LOGGER.info("DELETE " + BASE_PATH + "/{}", id, userId);
    try {
      groupService.deleteGroup(id, userId);
    } catch (NotFoundException e) {
      HttpStatus status = HttpStatus.NOT_FOUND;
      logClientError(status, "Group to delete not found", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    }
  }

  /**
  * Deleting member user in group, only possible by host.
  *
  * @param groupId  the id of the group
  * @param hostId   the id of the host
  * @param memberId the id of member to be deleted
  */
  @Secured("ROLE_ADMIN")
  @DeleteMapping("{groupId}/{memberId}/{hostId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(security = @SecurityRequirement(name = "apiKey"))
  public void deleteMemberOfGroup(@PathVariable Long groupId, @PathVariable Long memberId, 
      @PathVariable Long hostId) {
    LOGGER.info("DELETE " + BASE_PATH + "/{}", groupId, memberId, hostId);
    try {
      groupService.deleteMember(groupId, hostId, memberId);
    } catch (NotFoundException e) {
      HttpStatus status = HttpStatus.NOT_FOUND;
      logClientError(status, "Group to delete not found", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    }
  }

  /**
  * Searching for member of group.
  *
  * @param groupId    the id of the group
  * @param memberName the id of the member of group
  * @return list of matched user
  */
  @Secured("ROLE_ADMIN")
  @RequestMapping(value = "searchGroupMember/{groupId}/{memberName}", method = RequestMethod.GET)
  @ResponseStatus(HttpStatus.OK)
  public Optional<ApplicationUser> searchGroupMember(@PathVariable Long groupId, 
      @PathVariable String memberName) {
    LOGGER.info("GET " + BASE_PATH + "searchGroupMember/{}", groupId, memberName);
    return groupService.searchForMember(groupId, memberName);
  }

  private void logClientError(HttpStatus status, String message, Exception e) {
    LOGGER.warn("{} {}: {}: {}", status.value(), message, 
        e.getClass().getSimpleName(), e.getMessage());
  }
  
}
