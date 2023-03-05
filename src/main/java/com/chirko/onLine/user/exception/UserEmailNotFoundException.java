package com.chirko.onLine.user.exception;

public class UserEmailNotFoundException extends Exception {
    private static final String DEFAULT_MESSAGE = "User with this email does not exist";
    public UserEmailNotFoundException() {
        super(DEFAULT_MESSAGE);
    }
}
