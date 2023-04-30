package com.chirko.onLine.dto.response.user;

import com.chirko.onLine.dto.response.ImgDto;

public record BaseUserDto(String id, String name, String surname, ImgDto avatar) {
}
