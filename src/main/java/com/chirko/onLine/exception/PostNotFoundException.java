package com.chirko.onLine.exception;

public class PostNotFoundException extends Exception {
    private static final String DEFAULT_MESSAGE = "Resource not found";
    public PostNotFoundException() {
        super(DEFAULT_MESSAGE);
    }
}
