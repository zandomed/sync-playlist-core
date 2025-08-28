package com.zandome.syncplaylist.user.domain.exceptions;

import com.zandome.syncplaylist.shared.domain.exceptions.BaseException;

public class UserAlreadyExistsException extends BaseException {
    
    public UserAlreadyExistsException(String email) {
        super("User with email '" + email + "' already exists", "USER_ALREADY_EXISTS");
    }
}