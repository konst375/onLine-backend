package com.chirko.onLine.dto.response;

import com.chirko.onLine.dto.response.user.BaseUserDto;

import java.util.Set;

public record FriendshipsDto(
        Set<BaseUserDto> friends,
        Set<BaseUserDto> followers,
        Set<BaseUserDto> requests
) {
}
