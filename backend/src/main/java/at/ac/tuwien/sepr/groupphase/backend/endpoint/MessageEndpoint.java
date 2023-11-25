package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MessageDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MessageInquiryDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SimpleMessageDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.MessageMapper;
import at.ac.tuwien.sepr.groupphase.backend.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import java.lang.invoke.MethodHandles;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Message Endpoint.
 *
 */
@RestController
@RequestMapping(value = "/api/v1/messages")
public class MessageEndpoint {

  private static final Logger LOGGER = 
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final MessageService messageService;
  private final MessageMapper messageMapper;

  @Autowired
  public MessageEndpoint(MessageService messageService, MessageMapper messageMapper) {
    this.messageService = messageService;
    this.messageMapper = messageMapper;
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
  public List<SimpleMessageDto> findAll() {
    LOGGER.info("GET /api/v1/messages");
    return messageMapper.messageToSimpleMessageDto(messageService.findAll());
  }

  /**
   * Find message endpoint.
   *
   * @param id - message id
   * @return  message
   */
  @Secured("ROLE_USER")
  @GetMapping(value = "/{id}")
  @Operation(summary = "Get detailed information about a specific message", 
      security = @SecurityRequirement(name = "apiKey"))
  public MessageDetailDto find(@PathVariable Long id) {
    LOGGER.info("GET /api/v1/messages/{}", id);
    return messageMapper.messageToDetailedMessageDto(messageService.findOne(id));
  }

  /**
   * Create message endpoint.
   *
   * @param messageDto - dto
   * @return published message
   */
  @Secured("ROLE_ADMIN")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  @Operation(summary = "Publish a new message", security = @SecurityRequirement(name = "apiKey"))
  public MessageDetailDto create(@Valid @RequestBody MessageInquiryDto messageDto) {
    LOGGER.info("POST /api/v1/messages body: {}", messageDto);
    return messageMapper.messageToDetailedMessageDto(
        messageService.publishMessage(messageMapper.messageInquiryDtoToMessage(messageDto)));
  }
}
