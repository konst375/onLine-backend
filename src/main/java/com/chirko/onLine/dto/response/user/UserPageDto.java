package com.chirko.onLine.dto.response.user;

import com.chirko.onLine.dto.response.img.BaseImgDto;
import com.chirko.onLine.dto.response.post.BasePostDto;

import java.time.LocalDate;
import java.util.Set;

public record UserPageDto(
        String id,
        String name,
        String surname,
        BaseImgDto avatar,
        BaseImgDto cover,
        LocalDate birthday,
        Set<BaseImgDto> images,
        Set<BasePostDto> posts) {
}
