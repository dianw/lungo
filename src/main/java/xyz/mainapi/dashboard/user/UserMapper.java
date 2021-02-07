package xyz.mainapi.dashboard.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import xyz.mainapi.dashboard.core.security.User;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "pictureUrl", source = "picture")
    User toUser(com.auth0.json.mgmt.users.User user);
}
