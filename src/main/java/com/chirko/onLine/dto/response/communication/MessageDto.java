package com.chirko.onLine.dto.response.communication;

import com.chirko.onLine.dto.response.user.BaseUserDto;

import java.sql.Timestamp;

public record MessageDto(
        String id,
        BaseUserDto sender,
        String text,
        Timestamp createdDate,
        Timestamp modifiedDate,
        boolean isViewed
) {
}
