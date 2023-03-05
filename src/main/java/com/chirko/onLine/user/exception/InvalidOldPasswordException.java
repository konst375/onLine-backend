package com.chirko.onLine.user.exception;

public class InvalidOldPasswordException extends Exception {
    private static final String DEFAULT_MESSAGE = "The old password you entered is invalid";

    public InvalidOldPasswordException() {
        super(DEFAULT_MESSAGE);
    }
}
