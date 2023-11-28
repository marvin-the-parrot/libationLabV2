package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MessageCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MessageDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.GroupMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.MessageMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationMessage;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.GroupService;
import at.ac.tuwien.sepr.groupphase.backend.service.MessageService;
import at.ac.tuwien.sepr.groupphase.backend.service.UserGroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.server.ResponseStatusException;

/**
 * Message Endpoint.
 */
@RestController
@RequestMapping(value = "/api/v1/messages")
public class MessageEndpoint {

    static final String BASE_PATH = "/api/v1/messages";
    private static final Logger LOGGER =
        LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final MessageService messageService;
    private final GroupService groupService;
    private final UserGroupService userGroupService;
    private final MessageMapper messageMapper;
    private final GroupMapper groupMapper;

    @Autowired
    public MessageEndpoint(MessageService messageService, GroupService groupService, MessageMapper messageMapper, GroupMapper groupMapper, UserGroupService userGroupService) {
        this.messageService = messageService;
        this.messageMapper = messageMapper;
        this.groupService = groupService;
        this.groupMapper = groupMapper;
        this.userGroupService = userGroupService;
    }

    /**
     * Find all messages endpoint.
     *
     * @return published messages
     */
    @Secured("ROLE_USER")
    @GetMapping
    @Operation(summary = "Get list of messages without details",
        security = @SecurityRequirement(name = "apiKey"))
    public List<MessageDetailDto> findAll() {
        LOGGER.info("GET /api/v1/messages");
        List<ApplicationMessage> messages = messageService.findAll();
        List<MessageDetailDto> returnMessages = new ArrayList<>();
        for (ApplicationMessage message : messages) {
            LOGGER.info("Message: {}", message);
            returnMessages.add(messageMapper.from(message, groupMapper.groupToGroupDetailDto(groupService.findOne(message.getGroupId()))));
        }
        return returnMessages;
    }

    /**
     * Create message endpoint.
     *
     * @param message - messageCreateDto
     * @return published message
     */
    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @Operation(summary = "Publish a new message", security = @SecurityRequirement(name = "apiKey"))
    public MessageDetailDto create(@Valid @RequestBody MessageCreateDto message) {
        LOGGER.info("POST /api/v1/messages body: {}", message);
        return messageMapper.from(messageService.create(message), groupMapper.groupToGroupDetailDto(groupService.findOne((message.getGroupId()))));
    }

    /**
     * Accept message and create UserGroup entry.
     *
     * @param message - messageCreateDto
     */
    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/accept")
    @Operation(summary = "Publish a new message", security = @SecurityRequirement(name = "apiKey"))
    public void create(@Valid @RequestBody MessageDetailDto message) throws ValidationException, ConflictException {
        LOGGER.info("POST /api/v1/messages body: {}", message);
        messageService.update(message);
        userGroupService.create(message.getGroup().getId());
    }

    /**
     * Update an existing message entry.
     *
     * @param id       the id of the message
     * @param toUpdate the message entry to update
     * @return the updated message entry
     * @throws ValidationException if the data is not valid
     * @throws ConflictException   if the data conflicts with existing data
     */
    @Secured("ROLE_USER")
    @PutMapping("{id}")
    @Operation(summary = "Update Message", security = @SecurityRequirement(name = "apiKey"))
    public MessageDetailDto update(@PathVariable long id, @RequestBody MessageDetailDto toUpdate)
        throws ValidationException, ConflictException {
        LOGGER.info("PUT " + BASE_PATH + "/{}", toUpdate);
        LOGGER.debug("Body of request:\n{}", toUpdate);
        toUpdate.setId(id);
        try {
            return messageMapper.from(messageService.update(toUpdate), groupMapper.groupToGroupDetailDto(groupService.findOne((toUpdate.getGroup().getId()))));
        } catch (NotFoundException e) {
            HttpStatus status = HttpStatus.NOT_FOUND;
            logClientError(status, "Group to update not found", e);
            throw new ResponseStatusException(status, e.getMessage(), e);
        }
    }

    /**
     * Deleting message entry by id.
     *
     * @param messageId the id of the host
     */
    @DeleteMapping("/{messageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(security = @SecurityRequirement(name = "apiKey"))
    public void delete(@PathVariable Long messageId)
        throws ValidationException, ConflictException {
        LOGGER.info("DELETE " + BASE_PATH + "/{}", messageId);
        try {
            messageService.delete(messageId);
        } catch (NotFoundException e) {
            HttpStatus status = HttpStatus.NOT_FOUND;
            logClientError(status, "Group to delete not found", e);
            throw new ResponseStatusException(status, e.getMessage(), e);
        }
    }

    private void logClientError(HttpStatus status, String message, Exception e) {
        LOGGER.warn("{} {}: {}: {}", status.value(), message,
            e.getClass().getSimpleName(), e.getMessage());
    }
}
