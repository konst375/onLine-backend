package com.chirko.onLine.dto.response;

import com.chirko.onLine.dto.response.user.BaseUserDto;

import java.sql.Timestamp;

public record CommentDto(String text, BaseUserDto user, Timestamp createdDate, Timestamp modifiedDate) {
}
