package com.chirko.onLine.dto.response.user;

import com.chirko.onLine.dto.response.ImgDto;
import com.chirko.onLine.dto.response.post.UserPostDto;

import java.time.LocalDate;
import java.util.Set;

public record UserPageDto(
        String id,
        String name,
        String surname,
        ImgDto avatar,
        ImgDto cover,
        LocalDate birthday,
        Set<ImgDto> images,
        Set<UserPostDto> posts) {
}
