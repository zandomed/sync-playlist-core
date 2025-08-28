package com.zandome.syncplaylist.user.domain.exceptions;

import com.zandome.syncplaylist.shared.domain.exceptions.BaseException;

public class UserNotFoundException extends BaseException {
    
    public UserNotFoundException(String email) {
        super("User with email '" + email + "' not found", "USER_NOT_FOUND");
    }
}