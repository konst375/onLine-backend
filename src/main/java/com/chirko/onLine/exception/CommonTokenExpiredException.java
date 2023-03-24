package com.chirko.onLine.exception;

public class CommonTokenExpiredException extends Exception {
    private static final String DEFAULT_MESSAGE = "Common token expired";

    public CommonTokenExpiredException() {
        super(DEFAULT_MESSAGE);
    }
}
