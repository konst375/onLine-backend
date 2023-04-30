package com.chirko.onLine.dto.response;

import java.sql.Timestamp;

public record ImgDto(String id, byte[] img, Timestamp createdDate) {
}
