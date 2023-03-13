package com.chirko.onLine.secure.token.commonToken.exception;

public class InvalidCommonTokenException extends Exception {
    private static final String DEFAULT_MESSAGE = "Invalid common token";

    public InvalidCommonTokenException() {
        super(DEFAULT_MESSAGE);
    }
}
