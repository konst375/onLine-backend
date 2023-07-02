package com.chirko.onLine.dto.response.post;

import com.chirko.onLine.dto.response.TagDto;
import com.chirko.onLine.dto.response.img.BaseImgDto;
import com.chirko.onLine.dto.response.user.BaseUserDto;
import com.chirko.onLine.entities.enums.Owner;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

public record BasePostDto(
        String id,
        String text,
        List<BaseImgDto> images,
        Set<TagDto> tags,
        Timestamp modifiedDate,
        Owner owner,
        int likes,
        Set<BaseUserDto> whoLiked,
        int comments) {
}
