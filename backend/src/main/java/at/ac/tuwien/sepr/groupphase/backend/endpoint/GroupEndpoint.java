package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import java.util.List;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.GroupDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.GroupMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.UserGroup;
import at.ac.tuwien.sepr.groupphase.backend.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;

@RestController
@RequestMapping(value = "/api/v1/groups")
public class GroupEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final GroupService groupService;
    private final GroupMapper groupMapper;

    @Autowired
    public GroupEndpoint(GroupService groupService, GroupMapper groupMapper) {
        this.groupService = groupService;
        this.groupMapper = groupMapper;
    }

    @Secured("ROLE_USER")
    @GetMapping(value = "/{id}")
    @Operation(summary = "Get detailed information about a specific group", security = @SecurityRequirement(name = "apiKey"))
    public GroupDetailDto find(@PathVariable Long id) {
        LOGGER.info("GET /api/v1/groups/{}", id);
        return groupMapper.groupToGroupDetailDto(groupService.findOne(id));
    }

    @RequestMapping(value = "deleteGroup/{groupId}/{hostId}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.OK)
	  public Boolean deleteGroup(@PathVariable Long groupId, @PathVariable Long hostId) {
	    return groupService.deleteGroup(groupId, hostId);
	  }

	@RequestMapping(value = "deleteMemberOfGroup/{groupId}/{hostId}/{memberId}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.OK)
	public Boolean deleteMemberOfGroup(@PathVariable Long groupId, @PathVariable Long hostId, @PathVariable Long memberId) {
	  return groupService.deleteMember(groupId, hostId, memberId);
	}

	@RequestMapping(value = "searchGroupMember/{groupId}/{memberName}", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public List<UserGroup> searchGroupMember(@PathVariable Long groupId, @PathVariable String memberName) {
	  return groupService.searchForMember(groupId, memberName);
	}
}
