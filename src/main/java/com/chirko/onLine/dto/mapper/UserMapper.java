package com.chirko.onLine.dto.mapper;

import com.chirko.onLine.dto.response.UserDto;
import com.chirko.onLine.dto.response.UserPageDto;
import com.chirko.onLine.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR, imports = {ImgMapper.class, PostMapper.class})
public interface UserMapper {
    UserDto userToUserDto(User entity);

    UserPageDto userToUserPageDto(User entity);
}
