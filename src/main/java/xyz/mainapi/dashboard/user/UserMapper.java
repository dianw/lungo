package xyz.mainapi.dashboard.user;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.auth0.json.mgmt.logevents.LogEvent;
import com.auth0.json.mgmt.users.User;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    String PROPS_USER_METADATA_PICTURE_URL = "pictureUrl";

    @Mapping(target = "pictureUrl", expression = "java(getPictureUrl(user))")
    UserData toUserData(User user);

    @Mapping(target = "ip", expression = "java(logEvent.getIP())")
    UserLogEventData toUserLogEventData(LogEvent logEvent);

    List<UserLogEventData> toUserLogEventDataList(List<LogEvent> logEvents);

    default UserLocationInfoData toUserLocationInfo(Map<String, Object> locationInfo) {
        return Optional.ofNullable(locationInfo)
            .map(map -> ImmutableUserLocationInfoData.builder()
                .cityName((String) map.get("city_name"))
                .continentCode((String) map.get("continent_name"))
                .countryCode((String) map.get("country_code"))
                .countryName((String) map.get("country_name"))
                .timeZone((String) map.get("time_zone"))
                .latitude((Double) map.get("latitude"))
                .longitude((Double) map.get("longitude"))
                .build())
            .orElse(null);
    }

    default String getPictureUrl(User user) {
        return Optional.ofNullable(user.getUserMetadata())
            .map(map -> (String) map.get(PROPS_USER_METADATA_PICTURE_URL))
            .orElse(user.getPicture());
    }
}
