package com.zandome.syncplaylist.user.application.usecases;

import com.zandome.syncplaylist.user.domain.model.enums.AuthProvider;
import com.zandome.syncplaylist.user.domain.ports.repositories.UserAuthenticationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LinkAuthMethodUseCase {
    
    private final UserAuthenticationRepository userAuthRepository;
    private final CreateEmailAuthenticationUseCase createEmailAuthenticationUseCase;
    private final CreateOAuthAuthenticationUseCase createOAuthAuthenticationUseCase;

    public boolean execute(String userId, String email, AuthProvider provider, String providerId, String password) {
        if (userAuthRepository.existsByEmailAndProvider(email, provider)) {
            return false;
        }
        
        if (provider != AuthProvider.EMAIL && userAuthRepository.existsByProviderIdAndProvider(providerId, provider)) {
            return false;
        }
        
        if (provider == AuthProvider.EMAIL) {
            createEmailAuthenticationUseCase.execute(userId, email, password);
        } else {
            createOAuthAuthenticationUseCase.execute(userId, email, provider, providerId);
        }
        
        return true;
    }
}