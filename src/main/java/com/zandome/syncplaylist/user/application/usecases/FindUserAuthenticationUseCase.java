package com.zandome.syncplaylist.user.application.usecases;

import com.zandome.syncplaylist.user.domain.model.entities.UserAuthentication;
import com.zandome.syncplaylist.user.domain.model.enums.AuthProvider;
import com.zandome.syncplaylist.user.domain.ports.repositories.UserAuthenticationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FindUserAuthenticationUseCase {

    private final UserAuthenticationRepository userAuthRepository;

    public Optional<UserAuthentication> findByEmailAndProvider(String email, AuthProvider provider) {
        return userAuthRepository.findByEmailAndProvider(email, provider);
    }

    public Optional<UserAuthentication> findByProviderIdAndProvider(String providerId, AuthProvider provider) {
        return userAuthRepository.findByProviderIdAndProvider(providerId, provider);
    }

    public List<UserAuthentication> getUserAuthMethods(String userId) {
        return userAuthRepository.findActiveByUserId(userId);
    }
}