package com.zandome.syncplaylist.user.infra.adapters.http.dtos.request;

public record RegisterHttpRequest(
        String name,
        String lastName,
        String email,
        String password) {
}