package com.chirko.onLine.dto.mapper;

import com.chirko.onLine.dto.response.CommentDto;
import com.chirko.onLine.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.Set;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR, imports = {UserMapper.class})
public interface CommentMapper {
    CommentDto commentToCommentDto(Comment entity);

    Set<CommentDto> commentsToCommentsDto(Set<Comment> comments);
}
