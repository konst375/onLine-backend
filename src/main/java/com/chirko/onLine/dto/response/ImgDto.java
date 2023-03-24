package com.chirko.onLine.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
public class ImgDto {
    UUID id;
    byte[] img;
    Timestamp createdDate;
}
