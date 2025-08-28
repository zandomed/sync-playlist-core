package com.zandome.syncplaylist.user.application.dtos.commands;

public record LoginUserCommand(
        String email,
        String password) {
}