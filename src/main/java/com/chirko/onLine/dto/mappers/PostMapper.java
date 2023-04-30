package com.chirko.onLine.dto.mappers;

import com.chirko.onLine.dto.response.post.BasePostDto;
import com.chirko.onLine.dto.response.post.CommunityPostDto;
import com.chirko.onLine.dto.response.post.UserPostDto;
import com.chirko.onLine.entities.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.Set;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR, imports = {ImgMapper.class, UserMapper.class, TagMapper.class, CommunityMapper.class})
public interface PostMapper {
    BasePostDto toBasePostDto(Post entity);

    Set<BasePostDto> toBasePostsDto(Set<Post> images);

    @Mapping(target = "post", source = "entity")
    UserPostDto toUserPostDto(Post entity);

    Set<UserPostDto> toUserPostsDto(Set<Post> posts);

    @Mapping(target = "post", source = "entity")
    CommunityPostDto toCommunityPostDto(Post entity);

    Set<CommunityPostDto> toCommunityPostsDto(Set<Post> posts);
}
