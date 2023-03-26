package com.chirko.onLine.exception;

import java.sql.Timestamp;

public record ResponseException(String message, ErrorCause cause, Timestamp timestamp) {
}
