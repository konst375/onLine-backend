package com.chirko.onLine.dto.response;

import java.sql.Timestamp;

public record NotificationDto(
        String id,
        Timestamp createdDate,
        Timestamp modifiedDate,
        String target,
        String type,
        boolean isViewed
) {
}
