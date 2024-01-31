package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MessageCountDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MessageCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MessageDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MessageSetReadDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.GroupMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.MessageMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationMessage;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.security.SecurityRolesEnum;
import at.ac.tuwien.sepr.groupphase.backend.service.GroupService;
import at.ac.tuwien.sepr.groupphase.backend.service.MessageService;
import at.ac.tuwien.sepr.groupphase.backend.service.UserGroupService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Arrays;
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

import static at.ac.tuwien.sepr.groupphase.backend.endpoint.MessageEndpoint.BASE_PATH;

/**
 * Message Endpoint.
 */
@RestController
@RequestMapping(value = BASE_PATH)
public class MessageEndpoint {

    static final String BASE_PATH = "/api/v1/messages";
    private static final Logger LOGGER =
        LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final String ROLE_USER = SecurityRolesEnum.Roles.ROLE_USER;
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
     * Get number of unread messages endpoint.
     *
     * @return number of unread messages
     */
    @Secured(ROLE_USER)
    @GetMapping("/count")
    @Operation(summary = "Get number of unread messages")
    public MessageCountDto getUnreadMessageCount() {
        LOGGER.info("GET " + BASE_PATH + "/count");
        return messageMapper.countToMessageCountDto(messageService.getUnreadMessageCount());
    }

    /**
     * Find all messages endpoint.
     *
     * @return published messages
     */
    @Secured(ROLE_USER)
    @GetMapping
    @Operation(summary = "Get list of messages without details")
    public List<MessageDetailDto> findAll() {
        LOGGER.info("GET " + BASE_PATH);
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
     */
    //@Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/create")
    @Operation(summary = "Publish a new message")
    public void create(@Valid @RequestBody MessageCreateDto message) {
        LOGGER.info("POST " + BASE_PATH + "/create/{}", message);
        LOGGER.debug("Body of request:\n{}", message);

        if (message == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Message is null");
        }
        try {
            messageMapper.from(messageService.create(message), groupMapper.groupToGroupDetailDto(groupService.findOne((message.getGroupId()))));
        } catch (NotFoundException e) {
            logClientError(HttpStatus.NOT_FOUND, "Failed to create message since ", e);
            HttpStatus status = HttpStatus.NOT_FOUND;
            throw new ResponseStatusException(status, e.getMessage(), e);
        } catch (ValidationException e) {
            logClientError(HttpStatus.UNPROCESSABLE_ENTITY, "Failed to create message since ", e);
            HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
            throw new ResponseStatusException(status, e.getMessage(), e);
        } catch (Exception e) {
            logClientError(HttpStatus.BAD_REQUEST, "Failed to create message", e);
            HttpStatus status = HttpStatus.BAD_REQUEST;
            throw new ResponseStatusException(status, e.getMessage(), e);
        }
    }

    /**
     * Accept message and create UserGroup entry.
     *
     * @param message - messageCreateDto
     */
    @Secured(ROLE_USER)
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/accept")
    @Operation(summary = "Accept a group invation")
    public void acceptGroupInvitation(@Valid @RequestBody MessageDetailDto message) {
        LOGGER.info("POST " + BASE_PATH + "/accept/{}", message);
        LOGGER.debug("Body of request:\n{}", message);

        try {
            messageService.delete(message.getId());
            userGroupService.create(message.getGroup().getId());
        } catch (NotFoundException e) {
            logClientError(HttpStatus.NOT_FOUND, "Failed to accept message since ", e);
            HttpStatus status = HttpStatus.NOT_FOUND;
            throw new ResponseStatusException(status, e.getMessage(), e);
        }
    }

    /**
     * Update an existing message entry.
     *
     * @param toUpdate the message entry to update
     * @return the updated message entry
     * @throws ValidationException if the data is not valid
     * @throws ConflictException   if the data conflicts with existing data
     */
    @Secured(ROLE_USER)
    @PutMapping("{id}")
    @Operation(summary = "Update Message")
    public MessageDetailDto update(@RequestBody MessageDetailDto toUpdate)
        throws ValidationException, ConflictException {
        LOGGER.info("PUT " + BASE_PATH + "/{}", toUpdate.getId());
        LOGGER.debug("Body of request:\n{}", toUpdate);
        try {
            return messageMapper.from(messageService.update(toUpdate), groupMapper.groupToGroupDetailDto(groupService.findOne((toUpdate.getGroup().getId()))));
        } catch (NotFoundException e) {
            HttpStatus status = HttpStatus.NOT_FOUND;
            logClientError(status, "Message to update not found", e);
            throw new ResponseStatusException(status, e.getMessage(), e);
        } catch (ValidationException e) {
            HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
            logClientError(status, "Message to update is invalid", e);
            throw new ResponseStatusException(status, e.getMessage(), e);
        }
    }

    @Secured(ROLE_USER)
    @PutMapping("/read")
    @Operation(summary = "Mark all messages as read")
    public void markAllAsRead(@RequestBody MessageSetReadDto[] messagesToSetRead) {
        LOGGER.info("PUT " + BASE_PATH + "/read/{}", Arrays.toString(messagesToSetRead));
        LOGGER.debug("Body of request:\n{}", Arrays.toString(messagesToSetRead));

        try {
            messageService.markAllAsRead(messagesToSetRead);
        } catch (NotFoundException e) {
            HttpStatus status = HttpStatus.NOT_FOUND;
            logClientError(status, "Message to update not found", e);
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
    @Operation(summary = "Delete Message")
    public void delete(@PathVariable Long messageId) {
        LOGGER.info("DELETE " + BASE_PATH + "/{}", messageId);
        try {
            messageService.delete(messageId);
        } catch (NotFoundException e) {
            HttpStatus status = HttpStatus.NOT_FOUND;
            logClientError(status, "Message to delete not found", e);
            throw new ResponseStatusException(status, e.getMessage(), e);
        }
    }

    private void logClientError(HttpStatus status, String message, Exception e) {
        LOGGER.warn("{} {}: {}: {}", status.value(), message,
            e.getClass().getSimpleName(), e.getMessage());
    }
}
