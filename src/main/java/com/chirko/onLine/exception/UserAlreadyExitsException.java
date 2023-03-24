package com.chirko.onLine.exception;

public class UserAlreadyExitsException extends Exception {
    private static final String DEFAULT_MESSAGE = "User with this email already exist";
    public UserAlreadyExitsException() {
        super(DEFAULT_MESSAGE);
    }
}
