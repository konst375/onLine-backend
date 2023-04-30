package com.chirko.onLine.dto.mappers;

import com.chirko.onLine.dto.response.post.UserPostDto;
import com.chirko.onLine.dto.response.user.BaseUserDto;
import com.chirko.onLine.dto.response.user.UserPageDto;
import com.chirko.onLine.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.Set;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR, imports = {ImgMapper.class})
@Named("UserMapper")
public interface UserMapper {
    @Named("BaseUserDto")
    BaseUserDto toBaseUserDto(User user);

    Set<BaseUserDto> toBaseUsersDto(Set<User> followers);

    @Named("UserPageDto")
    @Mapping(target = "posts", source = "posts")
    UserPageDto userToUserPageDto(User user, Set<UserPostDto> posts);
}
