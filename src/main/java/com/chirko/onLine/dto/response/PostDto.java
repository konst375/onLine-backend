package com.chirko.onLine.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
public class PostDto {
    String id;
    UserDto user;
    String text;
    List<ImgDto> imagesList;
    Timestamp modifiedDate;
}
