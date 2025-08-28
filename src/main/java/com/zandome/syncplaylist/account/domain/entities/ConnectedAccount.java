package com.zandome.syncplaylist.account.domain.entities;

import java.time.Instant;

import com.zandome.syncplaylist.account.domain.constants.Provider;

public class ConnectedAccount {
    private String id;
    private String userId;
    private Provider provider;
    private Instant expiresAt;
    private String accessToken;
    private String refreshToken;

    public boolean isExpired() {
        return expiresAt != null && expiresAt.isBefore(Instant.now().minusSeconds(30));
    }
}
