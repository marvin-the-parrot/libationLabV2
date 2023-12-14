package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.GroupDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.GroupOverviewDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationGroup;
import at.ac.tuwien.sepr.groupphase.backend.entity.UserGroupKey;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Group mapper.
 */
@Mapper
public interface GroupMapper {

    GroupOverviewDto grouptToGroupOverviewDto(ApplicationGroup applicationGroup);

    GroupDetailDto groupToGroupDetailDto(ApplicationGroup applicationGroup);

    @Mapping(target = "id", source = "userGroupKey")
    default Long map(UserGroupKey userGroupKey) {
        return userGroupKey.user; // Extract the id from UserGroupKey and return it
    }
}
