package com.chirko.onLine.exceptions;

public class TokenExpiredException extends Exception {
    private static final String DEFAULT_MESSAGE = "Token expired";
    public TokenExpiredException() {
        super(DEFAULT_MESSAGE);
    }
}
