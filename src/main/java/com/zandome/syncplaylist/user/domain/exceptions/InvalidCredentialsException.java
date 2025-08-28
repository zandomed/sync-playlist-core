package com.zandome.syncplaylist.user.domain.exceptions;

import com.zandome.syncplaylist.shared.domain.exceptions.BaseException;

public class InvalidCredentialsException extends BaseException {
    
    public InvalidCredentialsException() {
        super("Invalid email or password", "INVALID_CREDENTIALS");
    }
    
    public InvalidCredentialsException(String message) {
        super(message, "INVALID_CREDENTIALS");
    }
}