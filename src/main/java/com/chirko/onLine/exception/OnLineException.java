package com.chirko.onLine.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class OnLineException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Internal server error";
    private final ErrorCause errorCause;
    private final HttpStatus httpStatus;

    public OnLineException(ErrorCause errorCause, HttpStatus httpStatus) {
        super(DEFAULT_MESSAGE);
        this.errorCause = errorCause;
        this.httpStatus = httpStatus;
    }

    public OnLineException(String message, ErrorCause errorCause, HttpStatus httpStatus) {
        super(message);
        this.errorCause = errorCause;
        this.httpStatus = httpStatus;
    }
}
