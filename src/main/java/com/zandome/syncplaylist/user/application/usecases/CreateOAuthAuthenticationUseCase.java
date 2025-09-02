package com.zandome.syncplaylist.user.application.usecases;

import com.zandome.syncplaylist.user.domain.model.entities.UserAuthentication;
import com.zandome.syncplaylist.user.domain.model.enums.AuthProvider;
import com.zandome.syncplaylist.user.domain.ports.repositories.UserAuthenticationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CreateOAuthAuthenticationUseCase {
    
    private final UserAuthenticationRepository userAuthRepository;

    public UserAuthentication execute(String userId, String email, AuthProvider provider, String providerId) {
        var userAuth = UserAuthentication.builder()
                .userId(userId)
                .email(email)
                .provider(provider)
                .providerId(providerId)
                .createdAt(LocalDateTime.now())
                .lastUsedAt(LocalDateTime.now())
                .isActive(true)
                .build();
        
        return userAuthRepository.save(userAuth);
    }
}