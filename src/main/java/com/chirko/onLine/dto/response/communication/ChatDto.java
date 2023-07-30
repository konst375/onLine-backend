package com.chirko.onLine.dto.response.communication;

import com.chirko.onLine.dto.response.img.BaseImgDto;
import com.chirko.onLine.dto.response.user.BaseUserDto;

import java.sql.Timestamp;
import java.util.Set;

public record ChatDto(
        String id,
        String name,
        String admin,
        BaseImgDto avatar,
        Timestamp createdDate,
        Timestamp modifiedDate,
        Set<MessageDto> messages,
        Set<BaseUserDto> participants
) {
}
