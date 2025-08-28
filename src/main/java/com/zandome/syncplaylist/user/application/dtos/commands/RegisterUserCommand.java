package com.zandome.syncplaylist.user.application.dtos.commands;

public record RegisterUserCommand(
        String name,
        String lastName,
        String email,
        String password) {
}