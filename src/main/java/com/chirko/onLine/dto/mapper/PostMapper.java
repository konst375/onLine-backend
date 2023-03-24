package com.chirko.onLine.dto.mapper;

import com.chirko.onLine.dto.response.PostDto;
import com.chirko.onLine.entity.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR, imports = {PostMapper.class, ImgMapper.class})
public interface PostMapper {
    @Mapping(target = "id", source = "entity.id")
    @Mapping(target = "user", source = "entity.user")
    @Mapping(target = "text", source = "entity.text")
    @Mapping(target = "imagesList", source = "entity.imagesList")
    @Mapping(target = "modifiedDate", source = "entity.modifiedDate")
    PostDto postToPostDto(Post entity);
}
