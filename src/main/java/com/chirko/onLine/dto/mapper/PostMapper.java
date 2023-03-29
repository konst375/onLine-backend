package com.chirko.onLine.dto.mapper;

import com.chirko.onLine.dto.response.PostDto;
import com.chirko.onLine.dto.response.PostForKnownUserDto;
import com.chirko.onLine.entity.Post;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR, imports = {PostMapper.class, ImgMapper.class, UserMapper.class})
public interface PostMapper {
    PostDto postToPostDto(Post entity);

    List<PostDto> postsToPostsDto(List<Post> posts);

    PostForKnownUserDto postToPostForKnownUserDto(Post entity);

    List<PostForKnownUserDto> postsToPostsForKnownUserDto(Set<Post> images);
}
