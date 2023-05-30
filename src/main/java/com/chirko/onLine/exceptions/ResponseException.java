package com.chirko.onLine.exceptions;

import java.sql.Timestamp;

public record ResponseException(
        String message,
        ErrorCause cause,
        Timestamp timestamp) {
}
