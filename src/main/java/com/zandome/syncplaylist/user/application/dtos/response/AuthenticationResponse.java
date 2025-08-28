package com.zandome.syncplaylist.user.application.dtos.response;

import com.zandome.syncplaylist.user.domain.model.entities.User;

public record AuthenticationResponse(
                User user,
                String token) {
}