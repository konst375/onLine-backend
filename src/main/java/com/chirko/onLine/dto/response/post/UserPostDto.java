package com.chirko.onLine.dto.response.post;

import com.chirko.onLine.dto.response.user.BaseUserDto;

public record UserPostDto(BaseUserDto user, BasePostDto post) {
}
