package com.chirko.onLine.dto.mappers;

import com.chirko.onLine.dto.response.post.BasePostDto;
import com.chirko.onLine.dto.response.user.BaseUserDto;
import com.chirko.onLine.dto.response.user.UserPageDto;
import com.chirko.onLine.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR, imports = ImgMapper.class)
@Named("UserMapper")
public interface UserMapper {
    @Named("BaseUserDto")
    BaseUserDto toBaseUserDto(User user);

    Set<BaseUserDto> toBaseUsersDto(Set<User> followers);

    @Named("UserPageDto")
    @Mapping(target = "posts", source = "posts")
    UserPageDto userToUserPageDto(User user, Set<BasePostDto> posts);
}
