package com.chirko.onLine.dto.response.img;

import java.sql.Timestamp;

public record BaseImgDto(
        String id,
        byte[] img,
        Timestamp createdDate,
        Timestamp modifiedDate) {
}
