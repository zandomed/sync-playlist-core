package com.zandome.syncplaylist.user.application.usecases;

import com.zandome.syncplaylist.user.domain.model.entities.UserAuthentication;
import com.zandome.syncplaylist.user.domain.ports.repositories.UserAuthenticationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class UpdateLastUsedUseCase {
    
    private final UserAuthenticationRepository userAuthRepository;

    public void execute(String authId) {
        var auth = userAuthRepository.findByUserId(authId).stream()
                .filter(a -> a.getId().equals(authId))
                .findFirst();
        
        if (auth.isPresent()) {
            var updated = UserAuthentication.builder()
                    .id(auth.get().getId())
                    .userId(auth.get().getUserId())
                    .provider(auth.get().getProvider())
                    .providerId(auth.get().getProviderId())
                    .email(auth.get().getEmail())
                    .passwordHash(auth.get().getPasswordHash())
                    .createdAt(auth.get().getCreatedAt())
                    .lastUsedAt(LocalDateTime.now())
                    .isActive(auth.get().isActive())
                    .build();
            
            userAuthRepository.save(updated);
        }
    }
}