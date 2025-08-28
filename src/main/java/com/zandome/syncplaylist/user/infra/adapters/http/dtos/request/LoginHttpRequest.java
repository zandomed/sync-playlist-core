package com.zandome.syncplaylist.user.infra.adapters.http.dtos.request;

public record LoginHttpRequest(
        String email,
        String password) {
}