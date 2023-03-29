package com.chirko.onLine.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
public class PostForKnownUserDto {
    String id;
    String text;
    List<ImgDto> images;
    Timestamp modifiedDate;
}
