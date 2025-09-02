package com.zandome.syncplaylist.user.application.usecases;

import com.zandome.syncplaylist.user.domain.model.entities.UserAuthentication;
import com.zandome.syncplaylist.user.domain.model.enums.AuthProvider;
import com.zandome.syncplaylist.user.domain.ports.repositories.UserAuthenticationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CreateEmailAuthenticationUseCase {
    
    private final UserAuthenticationRepository userAuthRepository;
    private final PasswordEncoder passwordEncoder;

    public UserAuthentication execute(String userId, String email, String password) {
        var userAuth = UserAuthentication.builder()
                .userId(userId)
                .email(email)
                .provider(AuthProvider.EMAIL)
                .passwordHash(passwordEncoder.encode(password))
                .createdAt(LocalDateTime.now())
                .lastUsedAt(LocalDateTime.now())
                .isActive(true)
                .build();
        
        return userAuthRepository.save(userAuth);
    }
}