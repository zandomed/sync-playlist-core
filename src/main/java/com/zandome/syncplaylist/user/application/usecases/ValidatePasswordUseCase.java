package com.zandome.syncplaylist.user.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ValidatePasswordUseCase {
    
    private final PasswordEncoder passwordEncoder;

    public boolean execute(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}