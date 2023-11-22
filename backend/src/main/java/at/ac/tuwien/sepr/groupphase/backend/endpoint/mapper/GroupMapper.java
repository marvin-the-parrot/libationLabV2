package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.GroupDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Group;
import org.mapstruct.Mapper;

@Mapper
public interface GroupMapper {

    GroupDetailDto groupToGroupDetailDto(Group group);
}
