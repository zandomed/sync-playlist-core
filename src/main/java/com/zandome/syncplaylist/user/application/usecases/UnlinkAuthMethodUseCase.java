package com.zandome.syncplaylist.user.application.usecases;

import com.zandome.syncplaylist.user.domain.model.enums.AuthProvider;
import com.zandome.syncplaylist.user.domain.ports.repositories.UserAuthenticationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UnlinkAuthMethodUseCase {
    
    private final UserAuthenticationRepository userAuthRepository;
    private final FindUserAuthenticationUseCase findUserAuthenticationUseCase;

    public boolean execute(String userId, AuthProvider provider) {
        var authMethods = findUserAuthenticationUseCase.getUserAuthMethods(userId);
        
        if (authMethods.size() <= 1) {
            return false;
        }
        
        var authToRemove = authMethods.stream()
                .filter(auth -> auth.getProvider() == provider)
                .findFirst();
        
        if (authToRemove.isPresent()) {
            userAuthRepository.deleteById(authToRemove.get().getId());
            return true;
        }
        
        return false;
    }
}