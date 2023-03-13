package com.chirko.onLine.secure.token.commonToken.exception;

public class CommonTokenForSuchUserNotFoundException extends Exception {
    private static final String DEFAULT_MESSAGE = "Common token not found";

    public CommonTokenForSuchUserNotFoundException() {
        super(DEFAULT_MESSAGE);
    }
}
