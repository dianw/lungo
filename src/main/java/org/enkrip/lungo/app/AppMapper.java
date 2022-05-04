package org.enkrip.lungo.app;

import java.util.Optional;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
interface AppMapper {
    AppMapper INSTANCE = Mappers.getMapper(AppMapper.class);

    @Mapping(target = "id", expression = "java(entity.getId())")
    AppData toAppData(AppEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    AppEntity toAppEntity(AppData appData);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    AppEntity updateAppEntity(@MappingTarget AppEntity entity, AppData appData);

    default <T> T optionalToObject(Optional<T> optional) {
        return optional.orElse(null);
    }
}
