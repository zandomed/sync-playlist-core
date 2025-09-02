package com.zandome.syncplaylist.user.infra.adapters.http.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginHttpRequest(
                @NotBlank(message = "Email is required") @Email(message = "Please provide a valid email address") String email,
                @NotBlank(message = "Password is required") String password) {
}