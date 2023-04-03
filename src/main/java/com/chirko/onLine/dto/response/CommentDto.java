package com.chirko.onLine.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class CommentDto {
    String text;
    UserDto user;
    Timestamp createdDate;
    Timestamp modifiedDate;
}
