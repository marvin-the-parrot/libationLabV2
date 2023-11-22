package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.GroupDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationGroup;
import org.mapstruct.Mapper;

@Mapper
public interface GroupMapper {

    GroupDetailDto groupToGroupDetailDto(ApplicationGroup applicationGroup);
}
