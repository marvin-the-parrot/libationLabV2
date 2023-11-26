package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.GroupDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.GroupOverviewDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationGroup;
import org.mapstruct.Mapper;

/**
 * Group mapper.
 */
@Mapper
public interface GroupMapper {

    GroupOverviewDto grouptToGroupOverviewDto(ApplicationGroup applicationGroup);

    GroupDetailDto groupToGroupDetailDto(ApplicationGroup applicationGroup);
}
