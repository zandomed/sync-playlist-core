package com.zandome.syncplaylist.shared.domain.exceptions;

public class JwtException extends BaseException {
    
    public JwtException(String message) {
        super(message, "JWT_ERROR");
    }
    
    public JwtException(String message, Throwable cause) {
        super(message, "JWT_ERROR", cause);
    }
}