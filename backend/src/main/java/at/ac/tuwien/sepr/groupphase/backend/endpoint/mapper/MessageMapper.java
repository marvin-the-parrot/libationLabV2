package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.GroupDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MessageCountDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MessageDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationMessage;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Message mapper.
 */
@Mapper
public interface MessageMapper {

    MessageCountDto countToMessageCountDto(Long count);

    @Mapping(source = "message.id", target = "id")
    @Mapping(source = "message.text", target = "text")
    @Mapping(source = "group", target = "group")
    @Mapping(source = "message.isRead", target = "isRead")
    @Mapping(source = "message.sentAt", target = "sentAt")
    MessageDetailDto from(ApplicationMessage message, GroupDetailDto group);

}

