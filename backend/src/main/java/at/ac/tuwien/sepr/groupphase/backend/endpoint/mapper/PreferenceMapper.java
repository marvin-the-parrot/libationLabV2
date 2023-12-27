package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PreferenceListDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Preference;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface PreferenceMapper {

    PreferenceListDto preferenceToPreferenceListDto(Preference preference);

    List<PreferenceListDto> preferenceToPreferenceListDto(List<Preference> preference);

}
