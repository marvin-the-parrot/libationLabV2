package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MessageCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MessageDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationMessage;

import java.util.List;

import org.mapstruct.Mapper;

/**
 * Message mapper.
 */
@Mapper
public interface MessageMapper {

    MessageDetailDto messageToDetailedMessageDto(ApplicationMessage applicationMessage);

    List<MessageDetailDto> messageToDetailedMessageDto(List<ApplicationMessage> applicationMessage);

    ApplicationMessage messageCreateDtoToMessage(MessageCreateDto messageCreateDto);
}

