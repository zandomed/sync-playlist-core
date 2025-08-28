package com.zandome.syncplaylist.user.domain.ports.services;

public interface PasswordEncoderService {
    String encode(String rawPassword);

    boolean matches(String rawPassword, String encodedPassword);
}