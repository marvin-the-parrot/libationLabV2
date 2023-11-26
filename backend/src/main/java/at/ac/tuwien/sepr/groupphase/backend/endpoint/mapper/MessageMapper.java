package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.GroupDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MessageCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MessageDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationMessage;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Message mapper.
 */
@Mapper
public interface MessageMapper {

    MessageDetailDto messageToDetailedMessageDto(ApplicationMessage message);

    List<MessageDetailDto> messageToDetailedMessageDto(List<ApplicationMessage> message);

    ApplicationMessage messageCreateDtoToMessage(MessageCreateDto messageCreateDto);

    @Mapping(source = "message.id", target = "id")
    @Mapping(source = "group", target = "group")
    @Mapping(source = "message.isRead", target = "isRead")
    @Mapping(source = "message.sentAt", target = "sentAt")
    MessageDetailDto from(ApplicationMessage message, GroupDetailDto group);

}

