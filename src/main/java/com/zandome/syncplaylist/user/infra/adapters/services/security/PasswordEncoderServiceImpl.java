package com.zandome.syncplaylist.user.infra.adapters.services.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.zandome.syncplaylist.user.domain.ports.services.PasswordEncoderService;

@Service
@RequiredArgsConstructor
public class PasswordEncoderServiceImpl implements PasswordEncoderService {

    private final org.springframework.security.crypto.password.PasswordEncoder springPasswordEncoder;

    @Override
    public String encode(String rawPassword) {
        return springPasswordEncoder.encode(rawPassword);
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return springPasswordEncoder.matches(rawPassword, encodedPassword);
    }
}