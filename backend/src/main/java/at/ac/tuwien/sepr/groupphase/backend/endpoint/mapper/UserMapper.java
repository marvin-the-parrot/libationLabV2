package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserListGroupDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    UserListDto userToUserListDto(ApplicationUser user);

    List<UserListDto> userToUserListDto(List<ApplicationUser> user);

    List<UserListGroupDto> userToUserListGroupDto(List<ApplicationUser> user);
}
