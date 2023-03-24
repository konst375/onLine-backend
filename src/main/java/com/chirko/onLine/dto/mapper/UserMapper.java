package com.chirko.onLine.dto.mapper;

import com.chirko.onLine.dto.response.UserDto;
import com.chirko.onLine.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR, imports = {ImgMapper.class})
public interface UserMapper {

    @Mapping(target = "id", source = "entity.id")
    @Mapping(target = "name", source = "entity.name")
    @Mapping(target = "surname", source = "entity.surname")
    @Mapping(target = "avatar", source = "entity.avatar")
    UserDto userToUserDto(User entity);
}
