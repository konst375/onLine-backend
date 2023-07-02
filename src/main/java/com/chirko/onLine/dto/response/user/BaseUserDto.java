package com.chirko.onLine.dto.response.user;

import com.chirko.onLine.dto.response.img.BaseImgDto;

public record BaseUserDto(
        String id,
        String name,
        String surname,
        BaseImgDto avatar) {
}
