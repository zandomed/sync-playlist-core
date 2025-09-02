package com.zandome.syncplaylist.user.domain.model.entities;

import com.zandome.syncplaylist.user.domain.model.enums.AuthProvider;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class UserAuthentication {
    private final String id;
    private final String userId;
    private final AuthProvider provider;
    private final String providerId;
    private final String email;
    private final String passwordHash;
    private final LocalDateTime createdAt;
    private final LocalDateTime lastUsedAt;
    private final boolean isActive;

    public boolean isEmailAuth() {
        return provider == AuthProvider.EMAIL;
    }

    public boolean isOAuthProvider() {
        return provider != AuthProvider.EMAIL;
    }
}