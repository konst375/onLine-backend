package com.chirko.onLine.token.commonToken.exception;

public class InvalidCommonToken extends Exception {
    private static final String DEFAULT_MESSAGE = "Invalid common token";

    public InvalidCommonToken() {
        super(DEFAULT_MESSAGE);
    }
}
