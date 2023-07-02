package com.chirko.onLine.dto.mappers;

import com.chirko.onLine.dto.response.CommentDto;
import com.chirko.onLine.entities.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.Set;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        imports = {UserMapper.class, ImgMapper.class}, uses = {UserMapper.class})
public interface CommentMapper {
    @Mapping(target = "likes", expression = "java(entity.getLikes().size())")
    @Mapping(target = "user", qualifiedByName = {"UserMapper", "BaseUserDto"})
    CommentDto toDto(Comment entity);

    Set<CommentDto> commentsToCommentsDto(Set<Comment> comments);
}
