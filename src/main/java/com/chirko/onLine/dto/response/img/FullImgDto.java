package com.chirko.onLine.dto.response.img;

import com.chirko.onLine.dto.response.CommentDto;
import com.chirko.onLine.dto.response.user.BaseUserDto;

import java.util.Set;

public record FullImgDto(
        BaseImgDto baseImgDto,
        int likes,
        Set<BaseUserDto> whoLiked,
        int commentsAmount,
        Set<CommentDto> comments) {
}
